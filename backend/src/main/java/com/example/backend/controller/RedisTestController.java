package com.example.backend.controller;

import com.example.backend.common.Result;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    public RedisTestController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/api/redis/test")
    public Result<String> redisTest() {
        redisTemplate.opsForValue().set("course:test", "redis success");
        String value = redisTemplate.opsForValue().get("course:test");
        return Result.success(value);
    }
}