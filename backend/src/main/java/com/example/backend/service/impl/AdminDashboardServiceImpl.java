package com.example.backend.service.impl;

import com.example.backend.mapper.DashboardMapper;
import com.example.backend.service.AdminDashboardService;
import com.example.backend.service.AuthService;
import com.example.backend.vo.AdminDashboardSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final DashboardMapper dashboardMapper;
    private final AuthService authService;

    @Override
    public AdminDashboardSummaryVO getSummary(String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限查看后台仪表盘");
        return AdminDashboardSummaryVO.builder()
                .totalUserCount(dashboardMapper.countUsers())
                .enabledUserCount(dashboardMapper.countUsersByStatus(1))
                .disabledUserCount(dashboardMapper.countUsersByStatus(0))
                .studentCount(dashboardMapper.countUsersByRole("STUDENT"))
                .reviewerCount(dashboardMapper.countUsersByRole("REVIEWER"))
                .adminCount(dashboardMapper.countUsersByRole("ADMIN"))
                .totalMaterialCount(dashboardMapper.countMaterials())
                .approvedMaterialCount(dashboardMapper.countMaterialsByAuditStatus("APPROVED"))
                .pendingMaterialCount(dashboardMapper.countMaterialsByAuditStatus("PENDING"))
                .rejectedMaterialCount(dashboardMapper.countMaterialsByAuditStatus("REJECTED"))
                .disabledMaterialCount(dashboardMapper.countMaterialsByStatus(0))
                .totalQuestionCount(dashboardMapper.countQuestions())
                .approvedQuestionCount(dashboardMapper.countQuestionsByAuditStatus("APPROVED"))
                .pendingQuestionCount(dashboardMapper.countQuestionsByAuditStatus("PENDING"))
                .rejectedQuestionCount(dashboardMapper.countQuestionsByAuditStatus("REJECTED"))
                .disabledQuestionCount(dashboardMapper.countQuestionsByStatus(0))
                .totalAnswerCount(dashboardMapper.countAnswers())
                .totalReplyCount(dashboardMapper.countReplies())
                .totalDownloadCount(dashboardMapper.countDownloads())
                .totalLikeCount(dashboardMapper.countLikes())
                .totalFavoriteCount(dashboardMapper.countFavorites())
                .pendingReportCount(dashboardMapper.countReportsByHandleStatus("PENDING"))
                .resolvedReportCount(dashboardMapper.countReportsByHandleStatus("RESOLVED"))
                .rejectedReportCount(dashboardMapper.countReportsByHandleStatus("REJECTED"))
                .aiAuditTotalCount(dashboardMapper.countAiAudits())
                .aiAuditPassCount(dashboardMapper.countAiAuditsByResult("PASS"))
                .aiAuditRejectCount(dashboardMapper.countAiAuditsByResult("REJECT"))
                .aiAuditRiskCount(dashboardMapper.countAiAuditsByResult("RISK"))
                .build();
    }
}
