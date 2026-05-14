package com.example.backend.service;

import com.example.backend.vo.MaterialListItemVO;
import com.example.backend.vo.QuestionListItemVO;

import java.util.List;

public interface HotContentService {

    List<MaterialListItemVO> listHotMaterials(Integer limit);

    List<QuestionListItemVO> listHotQuestions(Integer limit);
}
