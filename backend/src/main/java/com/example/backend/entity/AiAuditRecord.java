package com.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAuditRecord {

    private Long id;
    private String targetType;
    private Long targetId;
    private String auditResult;
    private BigDecimal riskScore;
    private String reason;
    private String modelName;
    private String requestContent;
    private String responseContent;
    private LocalDateTime createTime;
}
