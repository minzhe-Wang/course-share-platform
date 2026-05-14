package com.example.backend.service.impl;

import com.example.backend.mapper.MaterialMapper;
import com.example.backend.mapper.QuestionMapper;
import com.example.backend.service.HotContentService;
import com.example.backend.vo.MaterialListItemVO;
import com.example.backend.vo.QuestionListItemVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotContentServiceImpl implements HotContentService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final MaterialMapper materialMapper;
    private final QuestionMapper questionMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<MaterialListItemVO> listHotMaterials(Integer limit) {
        int safeLimit = normalizeLimit(limit);
        String cacheKey = RedisCacheServiceImpl.HOT_MATERIALS_KEY_PREFIX + safeLimit;
        List<MaterialListItemVO> cachedList = readCache(cacheKey, new TypeReference<>() {
        });
        if (cachedList != null) {
            return cachedList;
        }

        List<MaterialListItemVO> list = materialMapper.findHotMaterials(safeLimit);
        writeCache(cacheKey, list);
        return list;
    }

    @Override
    public List<QuestionListItemVO> listHotQuestions(Integer limit) {
        int safeLimit = normalizeLimit(limit);
        String cacheKey = RedisCacheServiceImpl.HOT_QUESTIONS_KEY_PREFIX + safeLimit;
        List<QuestionListItemVO> cachedList = readCache(cacheKey, new TypeReference<>() {
        });
        if (cachedList != null) {
            return cachedList;
        }

        List<QuestionListItemVO> list = questionMapper.findHotQuestions(safeLimit);
        writeCache(cacheKey, list);
        return list;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private <T> T readCache(String cacheKey, TypeReference<T> typeReference) {
        try {
            String cachedValue = redisTemplate.opsForValue().get(cacheKey);
            if (cachedValue == null) {
                return null;
            }
            return objectMapper.readValue(cachedValue, typeReference);
        } catch (Exception e) {
            return null;
        }
    }

    private void writeCache(String cacheKey, Object value) {
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(value), CACHE_TTL);
        } catch (Exception e) {
            // 热门内容缓存失败不影响接口主流程。
        }
    }
}
