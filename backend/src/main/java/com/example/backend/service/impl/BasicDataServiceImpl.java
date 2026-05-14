package com.example.backend.service.impl;

import com.example.backend.mapper.CourseCategoryMapper;
import com.example.backend.mapper.TagMapper;
import com.example.backend.service.BasicDataService;
import com.example.backend.vo.CourseCategoryVO;
import com.example.backend.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicDataServiceImpl implements BasicDataService {

    private final CourseCategoryMapper courseCategoryMapper;
    private final TagMapper tagMapper;

    @Override
    public List<CourseCategoryVO> listCategories() {
        return courseCategoryMapper.findEnabledCategories();
    }

    @Override
    public List<TagVO> listTags(String type) {
        return tagMapper.findEnabledTags(StringUtils.hasText(type) ? type.trim() : null);
    }
}
