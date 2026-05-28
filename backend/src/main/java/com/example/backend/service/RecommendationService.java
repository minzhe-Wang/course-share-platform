package com.example.backend.service;

import com.example.backend.vo.RecommendationItemVO;

import java.util.List;

public interface RecommendationService {

    List<RecommendationItemVO> recommendMaterials(Integer limit);

    List<RecommendationItemVO> recommendQuestions(Integer limit);
}
