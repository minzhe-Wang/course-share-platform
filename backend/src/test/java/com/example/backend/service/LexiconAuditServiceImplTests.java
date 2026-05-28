package com.example.backend.service;

import com.example.backend.exception.BusinessException;
import com.example.backend.service.impl.LexiconAuditServiceImpl;
import com.example.backend.vo.AiAuditResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LexiconAuditServiceImplTests {

    private LexiconAuditServiceImpl auditService;

    @BeforeEach
    void setUp() {
        auditService = new LexiconAuditServiceImpl();
    }

    @Test
    void auditPassesNormalSupportedContent() {
        AiAuditResultVO result = auditService.audit("QUESTION", "normal study content");

        assertThat(result.getAuditResult()).isEqualTo("PASS");
        assertThat(result.getRiskScore()).isEqualByComparingTo("5.0");
        assertThat(result.getReason()).isEqualTo("\u8bcd\u5e93\u5ba1\u6838\u901a\u8fc7");
    }

    @Test
    void auditRejectsHighRiskContent() {
        AiAuditResultVO result = auditService.audit("MATERIAL", "\u8fd9\u662f\u4e00\u6bb5\u5e7f\u544a\u5185\u5bb9");

        assertThat(result.getAuditResult()).isEqualTo("REJECT");
        assertThat(result.getRiskScore()).isEqualByComparingTo("95.0");
        assertThat(result.getReason()).isEqualTo("\u547d\u4e2d\u9ad8\u98ce\u9669\u8bcd\uff1a\u5e7f\u544a");
    }

    @Test
    void auditRejectsInsultContent() {
        AiAuditResultVO result = auditService.audit("QUESTION", "\u6211\u65e5\u4f60\u5988");

        assertThat(result.getAuditResult()).isEqualTo("REJECT");
        assertThat(result.getRiskScore()).isEqualByComparingTo("95.0");
        assertThat(result.getReason()).isEqualTo("\u547d\u4e2d\u9ad8\u98ce\u9669\u8bcd\uff1a\u6211\u65e5\u4f60\u5988");
    }

    @Test
    void auditMarksSuspiciousContentAsRisk() {
        AiAuditResultVO result = auditService.audit("QUESTION", "\u8bf7\u52a0\u5fae\u4fe1\u83b7\u53d6\u8d44\u6599");

        assertThat(result.getAuditResult()).isEqualTo("RISK");
        assertThat(result.getRiskScore()).isEqualByComparingTo("70.0");
        assertThat(result.getReason()).isEqualTo("\u547d\u4e2d\u53ef\u7591\u8bcd\uff1a\u52a0\u5fae\u4fe1");
    }

    @Test
    void auditRejectsUnsupportedTargetType() {
        assertThatThrownBy(() -> auditService.audit("COMMENT", "normal content"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("\u5ba1\u6838\u5bf9\u8c61\u7c7b\u578b\u4e0d\u652f\u6301");
    }

    @Test
    void auditRejectsBlankContent() {
        assertThatThrownBy(() -> auditService.audit("QUESTION", " "))
                .isInstanceOf(BusinessException.class)
                .hasMessage("\u5ba1\u6838\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a");
    }
}
