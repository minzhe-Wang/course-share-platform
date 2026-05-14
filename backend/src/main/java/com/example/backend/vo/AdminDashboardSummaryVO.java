package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardSummaryVO {

    private Long totalUserCount;
    private Long enabledUserCount;
    private Long disabledUserCount;
    private Long studentCount;
    private Long reviewerCount;
    private Long adminCount;

    private Long totalMaterialCount;
    private Long approvedMaterialCount;
    private Long pendingMaterialCount;
    private Long rejectedMaterialCount;
    private Long disabledMaterialCount;

    private Long totalQuestionCount;
    private Long approvedQuestionCount;
    private Long pendingQuestionCount;
    private Long rejectedQuestionCount;
    private Long disabledQuestionCount;

    private Long totalAnswerCount;
    private Long totalReplyCount;
    private Long totalDownloadCount;
    private Long totalLikeCount;
    private Long totalFavoriteCount;

    private Long pendingReportCount;
    private Long resolvedReportCount;
    private Long rejectedReportCount;

    private Long aiAuditTotalCount;
    private Long aiAuditPassCount;
    private Long aiAuditRejectCount;
    private Long aiAuditRiskCount;
}
