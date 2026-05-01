package com.example.backend.controller;

import com.example.backend.common.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DbTestController {

    private final JdbcTemplate jdbcTemplate;

    public DbTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/db/time")
    public Result<Map<String, Object>> dbTime() {
        Map<String, Object> result = jdbcTemplate.queryForMap("SELECT NOW() AS now_time");
        return Result.success(result);
    }
}