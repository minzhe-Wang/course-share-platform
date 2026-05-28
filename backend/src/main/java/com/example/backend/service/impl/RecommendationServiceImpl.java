package com.example.backend.service.impl;

import com.example.backend.mapper.MaterialMapper;
import com.example.backend.mapper.QuestionMapper;
import com.example.backend.service.RecommendationService;
import com.example.backend.vo.RecommendationItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final int DEFAULT_LIMIT = 6;
    private static final int MAX_LIMIT = 20;

    private final MaterialMapper materialMapper;
    private final QuestionMapper questionMapper;

    @Override
    public List<RecommendationItemVO> recommendMaterials(Integer limit) {
        return materialMapper.findRecommendedMaterials(normalizeLimit(limit));
    }

    @Override
    public List<RecommendationItemVO> recommendQuestions(Integer limit) {
        return questionMapper.findRecommendedQuestions(normalizeLimit(limit));
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
