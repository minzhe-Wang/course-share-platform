package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.service.BasicDataService;
import com.example.backend.vo.CourseCategoryVO;
import com.example.backend.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BasicDataController {

    private final BasicDataService basicDataService;

    @GetMapping("/api/categories")
    public Result<List<CourseCategoryVO>> listCategories() {
        return Result.success(basicDataService.listCategories());
    }

    @GetMapping("/api/tags")
    public Result<List<TagVO>> listTags(@RequestParam(required = false) String type) {
        return Result.success(basicDataService.listTags(type));
    }
}
