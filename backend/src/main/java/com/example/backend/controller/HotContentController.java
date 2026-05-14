package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.service.HotContentService;
import com.example.backend.vo.MaterialListItemVO;
import com.example.backend.vo.QuestionListItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HotContentController {

    private final HotContentService hotContentService;

    @GetMapping("/api/hot/materials")
    public Result<List<MaterialListItemVO>> listHotMaterials(@RequestParam(required = false) Integer limit) {
        return Result.success(hotContentService.listHotMaterials(limit));
    }

    @GetMapping("/api/hot/questions")
    public Result<List<QuestionListItemVO>> listHotQuestions(@RequestParam(required = false) Integer limit) {
        return Result.success(hotContentService.listHotQuestions(limit));
    }
}
