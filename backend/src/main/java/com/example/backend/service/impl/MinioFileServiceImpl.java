package com.example.backend.service.impl;

import com.example.backend.service.AuthService;
import com.example.backend.service.FileService;
import com.example.backend.vo.FileUploadVO;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioFileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_FILE_TYPES = Set.of("PDF", "DOC", "DOCX", "ZIP");

    private final MinioClient minioClient;
    private final AuthService authService;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    public FileUploadVO upload(MultipartFile file, String authorization) {
        authService.getEnabledUser(authorization);

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new RuntimeException("文件名不能为空");
        }

        String fileType = getFileType(originalFilename);
        if (!ALLOWED_FILE_TYPES.contains(fileType)) {
            throw new RuntimeException("文件类型不支持");
        }

        String fileKey = "materials/" + UUID.randomUUID() + "." + fileType.toLowerCase(Locale.ROOT);
        try {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }

        return FileUploadVO.builder()
                .originalFilename(originalFilename)
                .fileKey(fileKey)
                .fileUrl(buildFileUrl(fileKey))
                .fileType(fileType)
                .fileSize(file.getSize())
                .build();
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }

    private String getFileType(String originalFilename) {
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            throw new RuntimeException("文件类型不能为空");
        }
        return originalFilename.substring(dotIndex + 1).toUpperCase(Locale.ROOT);
    }

    private String buildFileUrl(String fileKey) {
        String normalizedEndpoint = endpoint.endsWith("/")
                ? endpoint.substring(0, endpoint.length() - 1)
                : endpoint;
        return normalizedEndpoint + "/" + bucketName + "/" + fileKey;
    }
}
