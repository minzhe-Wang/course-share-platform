package com.example.backend.service.impl;

import com.example.backend.service.AiAuditService;
import com.example.backend.vo.AiAuditResultVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MockAiAuditServiceImpl implements AiAuditService {

    private static final List<String> SENSITIVE_WORDS = List.of("广告", "违法", "诈骗");

    @Override
    public AiAuditResultVO audit(String targetType, String content) {
        if (!isSupportedTargetType(targetType)) {
            throw new RuntimeException("审核对象类型不支持");
        }
        if (!StringUtils.hasText(content)) {
            throw new RuntimeException("审核内容不能为空");
        }

        String hitWord = findHitWord(content);
        if (hitWord != null) {
            return AiAuditResultVO.builder()
                    .auditResult("REJECT")
                    .riskScore(new BigDecimal("95.0"))
                    .reason("命中敏感词：" + hitWord)
                    .build();
        }

        return AiAuditResultVO.builder()
                .auditResult("PASS")
                .riskScore(new BigDecimal("5.0"))
                .reason("Mock AI 审核通过")
                .build();
    }

    private boolean isSupportedTargetType(String targetType) {
        return "MATERIAL".equals(targetType)
                || "QUESTION".equals(targetType)
                || "ANSWER".equals(targetType)
                || "REPLY".equals(targetType);
    }

    private String findHitWord(String content) {
        for (String word : SENSITIVE_WORDS) {
            if (content.contains(word)) {
                return word;
            }
        }
        return null;
    }
}
