package com.example.backend.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemoMinioObjectInitializer implements CommandLineRunner {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public void run(String... args) throws Exception {
        try {
            ensureBucketExists();
            for (Map.Entry<String, String> entry : demoObjects().entrySet()) {
                byte[] bytes = entry.getValue().getBytes(StandardCharsets.UTF_8);
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(entry.getKey())
                        .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                        .contentType(resolveContentType(entry.getKey()))
                        .build());
            }
        } catch (Exception e) {
            log.warn("Skip demo MinIO object initialization: {}", e.getMessage());
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

    private Map<String, String> demoObjects() {
        return Map.of(
                "materials/demo-software-engineering.pdf",
                "Course Share demo file: software engineering acceptance template.\n",
                "materials/demo-database-review.pdf",
                "Course Share demo file: database final review outline.\n",
                "materials/demo-network-lab.docx",
                "Course Share demo file: computer network lab guide.\n",
                "materials/demo-data-structure.pdf",
                "Course Share demo file: data structure notes.\n",
                "materials/demo-java-web.zip",
                "Course Share demo file: Java Web starter project.\n",
                "materials/demo-rejected.pdf",
                "Course Share demo file: rejected material sample.\n"
        );
    }

    private String resolveContentType(String objectKey) {
        if (objectKey.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (objectKey.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        if (objectKey.endsWith(".zip")) {
            return "application/zip";
        }
        return "application/octet-stream";
    }
}
