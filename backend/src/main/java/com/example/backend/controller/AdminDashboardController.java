package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.service.AdminDashboardService;
import com.example.backend.vo.AdminDashboardSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/api/admin/dashboard/summary")
    public Result<AdminDashboardSummaryVO> getSummary(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminDashboardService.getSummary(authorization));
    }
}
