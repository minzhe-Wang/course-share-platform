package com.example.backend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AiAuditRecordListItemVO {

    private Long id;
    private String targetType;
    private Long targetId;
    private String auditResult;
    private BigDecimal riskScore;
    private String reason;
    private String modelName;
    private LocalDateTime createTime;
}
