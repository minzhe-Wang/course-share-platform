package com.example.backend.service;

import com.example.backend.service.impl.MockAiAuditServiceImpl;
import com.example.backend.vo.AiAuditResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MockAiAuditServiceImplTests {

    private MockAiAuditServiceImpl aiAuditService;

    @BeforeEach
    void setUp() {
        aiAuditService = new MockAiAuditServiceImpl();
    }

    @Test
    void auditPassesNormalSupportedContent() {
        AiAuditResultVO result = aiAuditService.audit("QUESTION", "normal study content");

        assertThat(result.getAuditResult()).isEqualTo("PASS");
        assertThat(result.getRiskScore()).isEqualByComparingTo("5.0");
        assertThat(result.getReason()).isEqualTo("Mock AI 审核通过");
    }

    @Test
    void auditRejectsSensitiveContent() {
        AiAuditResultVO result = aiAuditService.audit("MATERIAL", "这是一段广告内容");

        assertThat(result.getAuditResult()).isEqualTo("REJECT");
        assertThat(result.getRiskScore()).isEqualByComparingTo("95.0");
        assertThat(result.getReason()).isEqualTo("命中敏感词：广告");
    }

    @Test
    void auditRejectsUnsupportedTargetType() {
        assertThatThrownBy(() -> aiAuditService.audit("COMMENT", "normal content"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("审核对象类型不支持");
    }

    @Test
    void auditRejectsBlankContent() {
        assertThatThrownBy(() -> aiAuditService.audit("QUESTION", " "))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("审核内容不能为空");
    }
}
