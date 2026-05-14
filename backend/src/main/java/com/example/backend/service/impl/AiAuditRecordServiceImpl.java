package com.example.backend.service.impl;

import com.example.backend.mapper.AiAuditRecordMapper;
import com.example.backend.service.AiAuditRecordService;
import com.example.backend.service.AuthService;
import com.example.backend.vo.AiAuditRecordDetailVO;
import com.example.backend.vo.AiAuditRecordListItemVO;
import com.example.backend.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AiAuditRecordServiceImpl implements AiAuditRecordService {

    private static final Set<String> TARGET_TYPES = Set.of("MATERIAL", "QUESTION", "ANSWER", "REPLY");
    private static final Set<String> AUDIT_RESULTS = Set.of("PASS", "REJECT", "RISK");

    private final AiAuditRecordMapper aiAuditRecordMapper;
    private final AuthService authService;

    @Override
    public PageResultVO<AiAuditRecordListItemVO> listRecords(Integer pageNum, Integer pageSize,
                                                             String targetType, String auditResult,
                                                             String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限查看AI审核记录");
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        String normalizedTargetType = normalizeNullableTargetType(targetType);
        String normalizedAuditResult = normalizeNullableAuditResult(auditResult);
        Long total = aiAuditRecordMapper.countRecords(normalizedTargetType, normalizedAuditResult);
        List<AiAuditRecordListItemVO> list = aiAuditRecordMapper.findRecords(
                normalizedTargetType,
                normalizedAuditResult,
                pageQuery.pageSize(),
                pageQuery.offset()
        );
        return PageResultVO.<AiAuditRecordListItemVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public AiAuditRecordDetailVO getRecordDetail(Long id, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限查看AI审核记录");
        if (id == null) {
            throw new RuntimeException("AI审核记录不存在");
        }
        AiAuditRecordDetailVO detail = aiAuditRecordMapper.findDetailById(id);
        if (detail == null) {
            throw new RuntimeException("AI审核记录不存在");
        }
        return detail;
    }

    private String normalizeNullableTargetType(String targetType) {
        if (!StringUtils.hasText(targetType)) {
            return null;
        }
        String normalizedType = targetType.trim().toUpperCase();
        if (!TARGET_TYPES.contains(normalizedType)) {
            throw new RuntimeException("审核对象类型不支持");
        }
        return normalizedType;
    }

    private String normalizeNullableAuditResult(String auditResult) {
        if (!StringUtils.hasText(auditResult)) {
            return null;
        }
        String normalizedResult = auditResult.trim().toUpperCase();
        if (!AUDIT_RESULTS.contains(normalizedResult)) {
            throw new RuntimeException("审核结果不支持");
        }
        return normalizedResult;
    }

    private PageQuery normalizePage(Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        return new PageQuery(safePageSize, (safePageNum - 1) * safePageSize);
    }

    private record PageQuery(int pageSize, int offset) {
    }
}
