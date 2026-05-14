package com.example.backend.service.impl;

import com.example.backend.service.HealthCheckService;
import com.example.backend.vo.HealthCheckVO;
import com.example.backend.vo.HealthComponentVO;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthCheckServiceImpl implements HealthCheckService {

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public HealthCheckVO check() {
        List<HealthComponentVO> components = new ArrayList<>();
        components.add(checkApplication());
        components.add(checkDatabase());
        components.add(checkRedis());
        components.add(checkMinio());

        boolean allUp = components.stream().allMatch(component -> "UP".equals(component.getStatus()));
        return HealthCheckVO.builder()
                .status(allUp ? "UP" : "DOWN")
                .checkTime(LocalDateTime.now())
                .components(components)
                .build();
    }

    private HealthComponentVO checkApplication() {
        return HealthComponentVO.builder()
                .name("application")
                .status("UP")
                .message("backend running")
                .build();
    }

    private HealthComponentVO checkDatabase() {
        try {
            String value = jdbcTemplate.queryForObject("SELECT 'ok'", String.class);
            return HealthComponentVO.builder()
                    .name("mysql")
                    .status("UP")
                    .message(value)
                    .build();
        } catch (Exception e) {
            return down("mysql", e);
        }
    }

    private HealthComponentVO checkRedis() {
        try {
            String key = "course:health:redis";
            redisTemplate.opsForValue().set(key, "ok", Duration.ofSeconds(30));
            String value = redisTemplate.opsForValue().get(key);
            return HealthComponentVO.builder()
                    .name("redis")
                    .status("UP")
                    .message(value)
                    .build();
        } catch (Exception e) {
            return down("redis", e);
        }
    }

    private HealthComponentVO checkMinio() {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!bucketExists) {
                return HealthComponentVO.builder()
                        .name("minio")
                        .status("DOWN")
                        .message("bucket not found")
                        .build();
            }
            return HealthComponentVO.builder()
                    .name("minio")
                    .status("UP")
                    .message("bucket exists")
                    .build();
        } catch (Exception e) {
            return down("minio", e);
        }
    }

    private HealthComponentVO down(String name, Exception e) {
        return HealthComponentVO.builder()
                .name(name)
                .status("DOWN")
                .message(rootMessage(e))
                .build();
    }

    private String rootMessage(Exception e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }
}
