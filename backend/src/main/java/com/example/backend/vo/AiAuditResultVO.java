package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AiAuditResultVO {

    private String auditResult;
    private BigDecimal riskScore;
    private String reason;
}
