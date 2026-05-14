package com.example.backend.service.impl;

import com.example.backend.dto.ReportCreateDTO;
import com.example.backend.dto.ReportHandleDTO;
import com.example.backend.entity.Report;
import com.example.backend.entity.SysUser;
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
        authService.getEnabledReviewerOrAdmin(authorization, "无权限处理举报");
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
        authService.getEnabledReviewerOrAdmin(authorization, "无权限处理举报");
        if (id == null) {
            throw new RuntimeException("举报不存在");
        }
        ReportDetailVO detail = reportMapper.findDetailById(id);
        if (detail == null) {
            throw new RuntimeException("举报不存在");
        }
        return detail;
    }

    @Override
    @Transactional
    public void handleReport(Long id, ReportHandleDTO reportHandleDTO, String authorization) {
        SysUser handler = authService.getEnabledReviewerOrAdmin(authorization, "无权限处理举报");
        Report report = reportMapper.findById(id);
        if (report == null) {
            throw new RuntimeException("举报不存在");
        }
        if (!"PENDING".equals(report.getHandleStatus())) {
            throw new RuntimeException("举报已处理");
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
            throw new RuntimeException("举报对象类型不支持");
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
            throw new RuntimeException("举报对象不存在");
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
            throw new RuntimeException("处理状态不支持");
        }
        return normalizedStatus;
    }

    private String normalizeHandleStatus(String handleStatus) {
        String normalizedStatus = handleStatus.trim().toUpperCase();
        if (!"RESOLVED".equals(normalizedStatus) && !"REJECTED".equals(normalizedStatus)) {
            throw new RuntimeException("处理状态不支持");
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
