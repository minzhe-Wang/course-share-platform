package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.service.AiAuditRecordService;
import com.example.backend.vo.AiAuditRecordDetailVO;
import com.example.backend.vo.AiAuditRecordListItemVO;
import com.example.backend.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminAiAuditRecordController {

    private final AiAuditRecordService aiAuditRecordService;

    @GetMapping("/api/admin/ai-audits")
    public Result<PageResultVO<AiAuditRecordListItemVO>> listRecords(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String auditResult,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(aiAuditRecordService.listRecords(pageNum, pageSize, targetType, auditResult, authorization));
    }

    @GetMapping("/api/admin/ai-audits/{id}")
    public Result<AiAuditRecordDetailVO> getRecordDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(aiAuditRecordService.getRecordDetail(id, authorization));
    }
}
