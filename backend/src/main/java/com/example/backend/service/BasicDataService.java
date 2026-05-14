package com.example.backend.service;

import com.example.backend.vo.CourseCategoryVO;
import com.example.backend.vo.TagVO;

import java.util.List;

public interface BasicDataService {

    List<CourseCategoryVO> listCategories();

    List<TagVO> listTags(String type);
}
