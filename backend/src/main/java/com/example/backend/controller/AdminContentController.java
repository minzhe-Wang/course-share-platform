package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.ContentStatusDTO;
import com.example.backend.service.AdminContentService;
import com.example.backend.vo.AdminAnswerItemVO;
import com.example.backend.vo.AdminMaterialItemVO;
import com.example.backend.vo.AdminQuestionItemVO;
import com.example.backend.vo.AdminReplyItemVO;
import com.example.backend.vo.PageResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    @GetMapping("/api/admin/materials")
    public Result<PageResultVO<AdminMaterialItemVO>> listMaterials(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminContentService.listMaterials(pageNum, pageSize, keyword, auditStatus, status, authorization));
    }

    @PutMapping("/api/admin/materials/{id}/status")
    public Result<Void> updateMaterialStatus(
            @PathVariable Long id,
            @RequestBody @Valid ContentStatusDTO contentStatusDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        adminContentService.updateMaterialStatus(id, contentStatusDTO, authorization);
        return Result.success();
    }

    @GetMapping("/api/admin/questions")
    public Result<PageResultVO<AdminQuestionItemVO>> listQuestions(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminContentService.listQuestions(pageNum, pageSize, keyword, auditStatus, status, authorization));
    }

    @PutMapping("/api/admin/questions/{id}/status")
    public Result<Void> updateQuestionStatus(
            @PathVariable Long id,
            @RequestBody @Valid ContentStatusDTO contentStatusDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        adminContentService.updateQuestionStatus(id, contentStatusDTO, authorization);
        return Result.success();
    }

    @GetMapping("/api/admin/answers")
    public Result<PageResultVO<AdminAnswerItemVO>> listAnswers(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminContentService.listAnswers(pageNum, pageSize, keyword, auditStatus, status, authorization));
    }

    @PutMapping("/api/admin/answers/{id}/status")
    public Result<Void> updateAnswerStatus(
            @PathVariable Long id,
            @RequestBody @Valid ContentStatusDTO contentStatusDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        adminContentService.updateAnswerStatus(id, contentStatusDTO, authorization);
        return Result.success();
    }

    @GetMapping("/api/admin/replies")
    public Result<PageResultVO<AdminReplyItemVO>> listReplies(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminContentService.listReplies(pageNum, pageSize, keyword, auditStatus, status, authorization));
    }

    @PutMapping("/api/admin/replies/{id}/status")
    public Result<Void> updateReplyStatus(
            @PathVariable Long id,
            @RequestBody @Valid ContentStatusDTO contentStatusDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        adminContentService.updateReplyStatus(id, contentStatusDTO, authorization);
        return Result.success();
    }
}
