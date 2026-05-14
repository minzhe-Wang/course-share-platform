package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.AiAuditTestDTO;
import com.example.backend.service.AiAuditService;
import com.example.backend.vo.AiAuditResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-audit")
@RequiredArgsConstructor
public class AiAuditController {

    private final AiAuditService aiAuditService;

    @PostMapping("/test")
    public Result<AiAuditResultVO> test(@RequestBody @Valid AiAuditTestDTO aiAuditTestDTO) {
        return Result.success(aiAuditService.audit(aiAuditTestDTO.getTargetType(), aiAuditTestDTO.getContent()));
    }
}
