package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.BasicStatusDTO;
import com.example.backend.dto.CourseCategorySaveDTO;
import com.example.backend.dto.TagSaveDTO;
import com.example.backend.service.AdminBasicDataService;
import com.example.backend.vo.AdminCourseCategoryVO;
import com.example.backend.vo.AdminTagVO;
import com.example.backend.vo.CourseCategoryVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.TagVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminBasicDataController {

    private final AdminBasicDataService adminBasicDataService;

    @GetMapping("/api/admin/categories")
    public Result<PageResultVO<AdminCourseCategoryVO>> listCategories(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminBasicDataService.listCategories(pageNum, pageSize, keyword, status, authorization));
    }

    @PostMapping("/api/admin/categories")
    public Result<CourseCategoryVO> createCategory(
            @RequestBody @Valid CourseCategorySaveDTO courseCategorySaveDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminBasicDataService.createCategory(courseCategorySaveDTO, authorization));
    }

    @PutMapping("/api/admin/categories/{id}")
    public Result<CourseCategoryVO> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CourseCategorySaveDTO courseCategorySaveDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminBasicDataService.updateCategory(id, courseCategorySaveDTO, authorization));
    }

    @PutMapping("/api/admin/categories/{id}/status")
    public Result<Void> updateCategoryStatus(
            @PathVariable Long id,
            @RequestBody @Valid BasicStatusDTO basicStatusDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        adminBasicDataService.updateCategoryStatus(id, basicStatusDTO, authorization);
        return Result.success();
    }

    @GetMapping("/api/admin/tags")
    public Result<PageResultVO<AdminTagVO>> listTags(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminBasicDataService.listTags(pageNum, pageSize, keyword, type, status, authorization));
    }

    @PostMapping("/api/admin/tags")
    public Result<TagVO> createTag(
            @RequestBody @Valid TagSaveDTO tagSaveDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminBasicDataService.createTag(tagSaveDTO, authorization));
    }

    @PutMapping("/api/admin/tags/{id}")
    public Result<TagVO> updateTag(
            @PathVariable Long id,
            @RequestBody @Valid TagSaveDTO tagSaveDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(adminBasicDataService.updateTag(id, tagSaveDTO, authorization));
    }

    @PutMapping("/api/admin/tags/{id}/status")
    public Result<Void> updateTagStatus(
            @PathVariable Long id,
            @RequestBody @Valid BasicStatusDTO basicStatusDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        adminBasicDataService.updateTagStatus(id, basicStatusDTO, authorization);
        return Result.success();
    }
}
