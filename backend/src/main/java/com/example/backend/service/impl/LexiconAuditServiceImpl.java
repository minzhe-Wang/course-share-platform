package com.example.backend.service.impl;

import com.example.backend.exception.BusinessException;
import com.example.backend.service.AiAuditService;
import com.example.backend.vo.AiAuditResultVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LexiconAuditServiceImpl implements AiAuditService {

    private static final List<String> HIGH_RISK_WORDS = List.of(
            "\u5e7f\u544a", "\u8fdd\u6cd5", "\u8bc8\u9a97", "\u6d89\u653f", "\u8272\u60c5", "\u66b4\u529b",
            "\u4ee3\u5199", "\u7b54\u6848\u552e\u5356", "\u5f15\u6d41", "\u8d4c\u535a",
            "\u6211\u65e5\u4f60\u5988", "\u65e5\u4f60\u5988", "\u4f60\u5988\u903c", "\u64cd\u4f60\u5988",
            "\u8349\u4f60\u5988", "\u50bb\u903c", "\u715e\u7b14", "\u5988\u7684", "\u6eda\u86cb",
            "\u53bb\u6b7b", "\u6b7b\u5168\u5bb6", "\u5783\u573e\u4e1c\u897f"
    );

    private static final List<String> SUSPICIOUS_WORDS = List.of(
            "\u52a0\u5fae\u4fe1", "\u8054\u7cfb\u65b9\u5f0f", "\u79c1\u804a", "\u517c\u804c",
            "\u8fd4\u73b0", "\u7834\u89e3", "\u5916\u6302", "\u8d44\u6e90\u552e\u5356",
            "\u9a82\u4eba", "\u8fb1\u9a82", "\u4eba\u8eab\u653b\u51fb"
    );

    @Override
    public AiAuditResultVO audit(String targetType, String content) {
        if (!isSupportedTargetType(targetType)) {
            throw new BusinessException(400, "\u5ba1\u6838\u5bf9\u8c61\u7c7b\u578b\u4e0d\u652f\u6301");
        }
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(400, "\u5ba1\u6838\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a");
        }

        String normalizedContent = content.toLowerCase();
        String highRiskWord = findHitWord(normalizedContent, HIGH_RISK_WORDS);
        if (highRiskWord != null) {
            return AiAuditResultVO.builder()
                    .auditResult("REJECT")
                    .riskScore(new BigDecimal("95.0"))
                    .reason("\u547d\u4e2d\u9ad8\u98ce\u9669\u8bcd\uff1a" + highRiskWord)
                    .build();
        }

        String suspiciousWord = findHitWord(normalizedContent, SUSPICIOUS_WORDS);
        if (suspiciousWord != null) {
            return AiAuditResultVO.builder()
                    .auditResult("RISK")
                    .riskScore(new BigDecimal("70.0"))
                    .reason("\u547d\u4e2d\u53ef\u7591\u8bcd\uff1a" + suspiciousWord)
                    .build();
        }

        return AiAuditResultVO.builder()
                .auditResult("PASS")
                .riskScore(new BigDecimal("5.0"))
                .reason("\u8bcd\u5e93\u5ba1\u6838\u901a\u8fc7")
                .build();
    }

    private boolean isSupportedTargetType(String targetType) {
        return "MATERIAL".equals(targetType)
                || "QUESTION".equals(targetType)
                || "ANSWER".equals(targetType)
                || "REPLY".equals(targetType);
    }

    private String findHitWord(String content, List<String> words) {
        for (String word : words) {
            if (content.contains(word.toLowerCase())) {
                return word;
            }
        }
        return null;
    }
}
