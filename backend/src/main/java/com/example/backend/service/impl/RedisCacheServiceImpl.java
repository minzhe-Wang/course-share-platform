package com.example.backend.service.impl;

import com.example.backend.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements CacheService {

    public static final String HOT_MATERIALS_KEY_PREFIX = "course:hot:materials:";
    public static final String HOT_QUESTIONS_KEY_PREFIX = "course:hot:questions:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void evictHotMaterials() {
        deleteByPattern(HOT_MATERIALS_KEY_PREFIX + "*");
    }

    @Override
    public void evictHotQuestions() {
        deleteByPattern(HOT_QUESTIONS_KEY_PREFIX + "*");
    }

    @Override
    public void evictAllHotContent() {
        evictHotMaterials();
        evictHotQuestions();
    }

    private void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            // 缓存清理失败不影响业务主流程。
        }
    }
}
