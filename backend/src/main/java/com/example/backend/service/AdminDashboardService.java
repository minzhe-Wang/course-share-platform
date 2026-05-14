package com.example.backend.service;

import com.example.backend.vo.AdminDashboardSummaryVO;

public interface AdminDashboardService {

    AdminDashboardSummaryVO getSummary(String authorization);
}
