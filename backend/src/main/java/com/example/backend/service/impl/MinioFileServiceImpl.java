package com.example.backend.service.impl;

import com.example.backend.exception.BusinessException;
import com.example.backend.service.AuthService;
import com.example.backend.service.FileService;
import com.example.backend.vo.FileUploadVO;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Value("${minio.public-endpoint:${minio.endpoint}}")
    private String publicEndpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Override
    public FileUploadVO upload(MultipartFile file, String authorization) {
        authService.getEnabledUser(authorization);

        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "\u4e0a\u4f20\u6587\u4ef6\u4e0d\u80fd\u4e3a\u7a7a");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new BusinessException(400, "\u6587\u4ef6\u540d\u4e0d\u80fd\u4e3a\u7a7a");
        }

        String fileType = getFileType(originalFilename);
        if (!ALLOWED_FILE_TYPES.contains(fileType)) {
            throw new BusinessException(400, "\u6587\u4ef6\u7c7b\u578b\u4e0d\u652f\u6301\uff0c\u4ec5\u652f\u6301 PDF\u3001DOC\u3001DOCX\u3001ZIP");
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
            throw new BusinessException("\u6587\u4ef6\u4e0a\u4f20\u5931\u8d25\uff1a" + e.getMessage());
        }

        return FileUploadVO.builder()
                .originalFilename(originalFilename)
                .fileKey(fileKey)
                .fileUrl(buildFileUrl(fileKey))
                .fileType(fileType)
                .fileSize(file.getSize())
                .build();
    }

    @Override
    public String getDownloadUrl(String fileKey) {
        if (!StringUtils.hasText(fileKey)) {
            throw new BusinessException(400, "\u6587\u4ef6 Key \u4e0d\u80fd\u4e3a\u7a7a");
        }
        return "/api/files/download?fileKey=" + URLEncoder.encode(fileKey, StandardCharsets.UTF_8);
    }

    @Override
    public void writeObject(String fileKey, HttpServletResponse response) {
        if (!StringUtils.hasText(fileKey)) {
            throw new BusinessException(400, "\u6587\u4ef6 Key \u4e0d\u80fd\u4e3a\u7a7a");
        }
        try {
            response.setContentType(resolveContentType(fileKey));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileKey.substring(fileKey.lastIndexOf('/') + 1) + "\"");
            try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileKey)
                    .build())) {
                inputStream.transferTo(response.getOutputStream());
            }
        } catch (Exception e) {
            throw new BusinessException("\u6587\u4ef6\u4e0b\u8f7d\u5931\u8d25\uff1a" + e.getMessage());
        }
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
            throw new BusinessException(400, "\u6587\u4ef6\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a");
        }
        return originalFilename.substring(dotIndex + 1).toUpperCase(Locale.ROOT);
    }

    private String buildFileUrl(String fileKey) {
        String normalizedEndpoint = publicEndpoint.endsWith("/")
                ? publicEndpoint.substring(0, publicEndpoint.length() - 1)
                : publicEndpoint;
        return normalizedEndpoint + "/" + bucketName + "/" + fileKey;
    }

    private String resolveContentType(String objectKey) {
        String lowerKey = objectKey.toLowerCase(Locale.ROOT);
        if (lowerKey.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (lowerKey.endsWith(".doc")) {
            return "application/msword";
        }
        if (lowerKey.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        if (lowerKey.endsWith(".zip")) {
            return "application/zip";
        }
        return "application/octet-stream";
    }
}
