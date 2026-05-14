package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.service.HealthCheckService;
import com.example.backend.vo.HealthCheckVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    @GetMapping("/api/health")
    public Result<HealthCheckVO> health() {
        return Result.success(healthCheckService.check());
    }
}
