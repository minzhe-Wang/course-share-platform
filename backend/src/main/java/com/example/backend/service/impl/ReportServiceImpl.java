package com.example.backend.service.impl;

import com.example.backend.dto.ReportCreateDTO;
import com.example.backend.dto.ReportHandleDTO;
import com.example.backend.entity.Report;
import com.example.backend.entity.SysUser;
import com.example.backend.exception.BusinessException;
import com.example.backend.mapper.ReportMapper;
import com.example.backend.service.AuthService;
import com.example.backend.service.ReportService;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.ReportCreateVO;
import com.example.backend.vo.ReportDetailVO;
import com.example.backend.vo.ReportListItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private static final Set<String> TARGET_TYPES = Set.of("MATERIAL", "QUESTION", "ANSWER", "REPLY");

    private final ReportMapper reportMapper;
    private final AuthService authService;

    @Override
    @Transactional
    public ReportCreateVO createReport(ReportCreateDTO reportCreateDTO, String authorization) {
        SysUser user = authService.getEnabledUser(authorization);
        String targetType = normalizeTargetType(reportCreateDTO.getTargetType());
        String snapshot = findTargetSnapshot(targetType, reportCreateDTO.getTargetId());

        Report report = Report.builder()
                .targetType(targetType)
                .targetId(reportCreateDTO.getTargetId())
                .targetSnapshot(snapshot)
                .reportUserId(user.getId())
                .reason(reportCreateDTO.getReason().trim())
                .handleStatus("PENDING")
                .build();
        reportMapper.insert(report);

        return ReportCreateVO.builder()
                .reportId(report.getId())
                .build();
    }

    @Override
    public PageResultVO<ReportListItemVO> listReports(Integer pageNum, Integer pageSize,
                                                      String handleStatus, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "\u65e0\u6743\u9650\u5904\u7406\u4e3e\u62a5");
        String normalizedStatus = normalizeNullableHandleStatus(handleStatus);
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int offset = (safePageNum - 1) * safePageSize;

        Long total = reportMapper.countReports(normalizedStatus);
        List<ReportListItemVO> list = reportMapper.findReports(normalizedStatus, safePageSize, offset);
        return PageResultVO.<ReportListItemVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public ReportDetailVO getReportDetail(Long id, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "\u65e0\u6743\u9650\u5904\u7406\u4e3e\u62a5");
        if (id == null) {
            throw new BusinessException(404, "\u4e3e\u62a5\u4e0d\u5b58\u5728");
        }
        ReportDetailVO detail = reportMapper.findDetailById(id);
        if (detail == null) {
            throw new BusinessException(404, "\u4e3e\u62a5\u4e0d\u5b58\u5728");
        }
        return detail;
    }

    @Override
    @Transactional
    public void handleReport(Long id, ReportHandleDTO reportHandleDTO, String authorization) {
        SysUser handler = authService.getEnabledReviewerOrAdmin(authorization, "\u65e0\u6743\u9650\u5904\u7406\u4e3e\u62a5");
        Report report = reportMapper.findById(id);
        if (report == null) {
            throw new BusinessException(404, "\u4e3e\u62a5\u4e0d\u5b58\u5728");
        }
        if (!"PENDING".equals(report.getHandleStatus())) {
            throw new BusinessException(400, "\u4e3e\u62a5\u5df2\u5904\u7406");
        }

        String handleStatus = normalizeHandleStatus(reportHandleDTO.getHandleStatus());
        if ("RESOLVED".equals(handleStatus)) {
            disableTarget(report.getTargetType(), report.getTargetId());
        }

        reportMapper.updateHandleResult(
                id,
                handleStatus,
                handler.getId(),
                reportHandleDTO.getHandleResult().trim(),
                LocalDateTime.now()
        );
    }

    private String normalizeTargetType(String targetType) {
        String normalizedType = targetType.trim().toUpperCase();
        if (!TARGET_TYPES.contains(normalizedType)) {
            throw new BusinessException(400, "\u4e3e\u62a5\u5bf9\u8c61\u7c7b\u578b\u4e0d\u652f\u6301");
        }
        return normalizedType;
    }

    private String findTargetSnapshot(String targetType, Long targetId) {
        String snapshot;
        if ("MATERIAL".equals(targetType)) {
            snapshot = reportMapper.findMaterialSnapshot(targetId);
        } else if ("QUESTION".equals(targetType)) {
            snapshot = reportMapper.findQuestionSnapshot(targetId);
        } else if ("ANSWER".equals(targetType)) {
            snapshot = reportMapper.findAnswerSnapshot(targetId);
        } else {
            snapshot = reportMapper.findReplySnapshot(targetId);
        }
        if (!StringUtils.hasText(snapshot)) {
            throw new BusinessException(404, "\u4e3e\u62a5\u5bf9\u8c61\u4e0d\u5b58\u5728");
        }
        return snapshot.length() > 500 ? snapshot.substring(0, 500) : snapshot;
    }

    private String normalizeNullableHandleStatus(String handleStatus) {
        if (!StringUtils.hasText(handleStatus)) {
            return null;
        }
        String normalizedStatus = handleStatus.trim().toUpperCase();
        if (!"PENDING".equals(normalizedStatus)
                && !"RESOLVED".equals(normalizedStatus)
                && !"REJECTED".equals(normalizedStatus)) {
            throw new BusinessException(400, "\u5904\u7406\u72b6\u6001\u4e0d\u652f\u6301");
        }
        return normalizedStatus;
    }

    private String normalizeHandleStatus(String handleStatus) {
        String normalizedStatus = handleStatus.trim().toUpperCase();
        if (!"RESOLVED".equals(normalizedStatus) && !"REJECTED".equals(normalizedStatus)) {
            throw new BusinessException(400, "\u5904\u7406\u72b6\u6001\u4e0d\u652f\u6301");
        }
        return normalizedStatus;
    }

    private void disableTarget(String targetType, Long targetId) {
        if ("MATERIAL".equals(targetType)) {
            reportMapper.disableMaterial(targetId);
        } else if ("QUESTION".equals(targetType)) {
            reportMapper.disableQuestion(targetId);
        } else if ("ANSWER".equals(targetType)) {
            reportMapper.disableAnswer(targetId);
        } else if ("REPLY".equals(targetType)) {
            reportMapper.disableReply(targetId);
        }
    }
}
