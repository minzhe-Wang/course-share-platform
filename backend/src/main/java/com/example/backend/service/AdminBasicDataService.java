package com.example.backend.service;

import com.example.backend.dto.BasicStatusDTO;
import com.example.backend.dto.CourseCategorySaveDTO;
import com.example.backend.dto.TagSaveDTO;
import com.example.backend.vo.AdminCourseCategoryVO;
import com.example.backend.vo.AdminTagVO;
import com.example.backend.vo.CourseCategoryVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.TagVO;

public interface AdminBasicDataService {

    PageResultVO<AdminCourseCategoryVO> listCategories(Integer pageNum, Integer pageSize, String keyword,
                                                       Integer status, String authorization);

    CourseCategoryVO createCategory(CourseCategorySaveDTO courseCategorySaveDTO, String authorization);

    CourseCategoryVO updateCategory(Long id, CourseCategorySaveDTO courseCategorySaveDTO, String authorization);

    void updateCategoryStatus(Long id, BasicStatusDTO basicStatusDTO, String authorization);

    PageResultVO<AdminTagVO> listTags(Integer pageNum, Integer pageSize, String keyword,
                                      String type, Integer status, String authorization);

    TagVO createTag(TagSaveDTO tagSaveDTO, String authorization);

    TagVO updateTag(Long id, TagSaveDTO tagSaveDTO, String authorization);

    void updateTagStatus(Long id, BasicStatusDTO basicStatusDTO, String authorization);
}
