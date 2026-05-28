package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.service.RecommendationService;
import com.example.backend.vo.RecommendationItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/api/recommend/materials")
    public Result<List<RecommendationItemVO>> recommendMaterials(@RequestParam(required = false) Integer limit) {
        return Result.success(recommendationService.recommendMaterials(limit));
    }

    @GetMapping("/api/recommend/questions")
    public Result<List<RecommendationItemVO>> recommendQuestions(@RequestParam(required = false) Integer limit) {
        return Result.success(recommendationService.recommendQuestions(limit));
    }
}
