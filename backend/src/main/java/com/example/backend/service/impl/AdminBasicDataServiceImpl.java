package com.example.backend.service.impl;

import com.example.backend.dto.BasicStatusDTO;
import com.example.backend.dto.CourseCategorySaveDTO;
import com.example.backend.dto.TagSaveDTO;
import com.example.backend.entity.CourseCategory;
import com.example.backend.entity.Tag;
import com.example.backend.mapper.CourseCategoryMapper;
import com.example.backend.mapper.TagMapper;
import com.example.backend.service.AdminBasicDataService;
import com.example.backend.service.AuthService;
import com.example.backend.vo.AdminCourseCategoryVO;
import com.example.backend.vo.AdminTagVO;
import com.example.backend.vo.CourseCategoryVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminBasicDataServiceImpl implements AdminBasicDataService {

    private static final int NORMAL_STATUS = 1;
    private static final Set<String> TAG_TYPES = Set.of("GRADE", "TYPE", "SCENE");

    private final CourseCategoryMapper courseCategoryMapper;
    private final TagMapper tagMapper;
    private final AuthService authService;

    @Override
    public PageResultVO<AdminCourseCategoryVO> listCategories(Integer pageNum, Integer pageSize, String keyword,
                                                              Integer status, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理基础数据");
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        Integer normalizedStatus = normalizeNullableStatus(status);
        String normalizedKeyword = trimToNull(keyword);
        Long total = courseCategoryMapper.countAdminCategories(normalizedKeyword, normalizedStatus);
        List<AdminCourseCategoryVO> list = courseCategoryMapper.findAdminCategories(
                normalizedKeyword,
                normalizedStatus,
                pageQuery.pageSize(),
                pageQuery.offset()
        );
        return PageResultVO.<AdminCourseCategoryVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public CourseCategoryVO createCategory(CourseCategorySaveDTO courseCategorySaveDTO, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理基础数据");
        CourseCategory category = buildCategory(null, courseCategorySaveDTO);
        checkCategoryName(category.getName(), 0L);
        category.setStatus(NORMAL_STATUS);
        courseCategoryMapper.insert(category);
        return toCategoryVO(category);
    }

    @Override
    public CourseCategoryVO updateCategory(Long id, CourseCategorySaveDTO courseCategorySaveDTO, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理基础数据");
        CourseCategory oldCategory = getCategory(id);
        CourseCategory category = buildCategory(oldCategory.getId(), courseCategorySaveDTO);
        checkCategoryName(category.getName(), category.getId());
        courseCategoryMapper.update(category);
        return toCategoryVO(courseCategoryMapper.findById(id));
    }

    @Override
    public void updateCategoryStatus(Long id, BasicStatusDTO basicStatusDTO, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理基础数据");
        getCategory(id);
        courseCategoryMapper.updateStatus(id, normalizeRequiredStatus(basicStatusDTO.getStatus()));
    }

    @Override
    public PageResultVO<AdminTagVO> listTags(Integer pageNum, Integer pageSize, String keyword,
                                             String type, Integer status, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理基础数据");
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        String normalizedKeyword = trimToNull(keyword);
        String normalizedType = normalizeNullableTagType(type);
        Integer normalizedStatus = normalizeNullableStatus(status);
        Long total = tagMapper.countAdminTags(normalizedKeyword, normalizedType, normalizedStatus);
        List<AdminTagVO> list = tagMapper.findAdminTags(
                normalizedKeyword,
                normalizedType,
                normalizedStatus,
                pageQuery.pageSize(),
                pageQuery.offset()
        );
        return PageResultVO.<AdminTagVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public TagVO createTag(TagSaveDTO tagSaveDTO, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理基础数据");
        Tag tag = buildTag(null, tagSaveDTO);
        checkTagNameAndType(tag.getName(), tag.getType(), 0L);
        tag.setStatus(NORMAL_STATUS);
        tagMapper.insert(tag);
        return toTagVO(tag);
    }

    @Override
    public TagVO updateTag(Long id, TagSaveDTO tagSaveDTO, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理基础数据");
        Tag oldTag = getTag(id);
        Tag tag = buildTag(oldTag.getId(), tagSaveDTO);
        checkTagNameAndType(tag.getName(), tag.getType(), tag.getId());
        tagMapper.update(tag);
        return toTagVO(tagMapper.findById(id));
    }

    @Override
    public void updateTagStatus(Long id, BasicStatusDTO basicStatusDTO, String authorization) {
        authService.getEnabledAdmin(authorization, "无权限管理基础数据");
        getTag(id);
        tagMapper.updateStatus(id, normalizeRequiredStatus(basicStatusDTO.getStatus()));
    }

    private CourseCategory buildCategory(Long id, CourseCategorySaveDTO dto) {
        Integer sortNo = dto.getSortNo();
        if (sortNo == null || sortNo < 0) {
            throw new RuntimeException("排序号不能小于0");
        }
        return CourseCategory.builder()
                .id(id)
                .name(dto.getName().trim())
                .type(dto.getType().trim())
                .sortNo(sortNo)
                .build();
    }

    private Tag buildTag(Long id, TagSaveDTO dto) {
        String type = normalizeRequiredTagType(dto.getType());
        return Tag.builder()
                .id(id)
                .name(dto.getName().trim())
                .type(type)
                .build();
    }

    private CourseCategory getCategory(Long id) {
        if (id == null) {
            throw new RuntimeException("课程分类不存在");
        }
        CourseCategory category = courseCategoryMapper.findById(id);
        if (category == null) {
            throw new RuntimeException("课程分类不存在");
        }
        return category;
    }

    private Tag getTag(Long id) {
        if (id == null) {
            throw new RuntimeException("标签不存在");
        }
        Tag tag = tagMapper.findById(id);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        return tag;
    }

    private void checkCategoryName(String name, Long excludeId) {
        if (courseCategoryMapper.countByNameExcludeId(name, excludeId) > 0) {
            throw new RuntimeException("课程分类名称已存在");
        }
    }

    private void checkTagNameAndType(String name, String type, Long excludeId) {
        if (tagMapper.countByNameAndTypeExcludeId(name, type, excludeId) > 0) {
            throw new RuntimeException("同类型标签名称已存在");
        }
    }

    private Integer normalizeNullableStatus(Integer status) {
        if (status == null) {
            return null;
        }
        return normalizeRequiredStatus(status);
    }

    private Integer normalizeRequiredStatus(Integer status) {
        if (!Integer.valueOf(0).equals(status) && !Integer.valueOf(1).equals(status)) {
            throw new RuntimeException("状态不支持");
        }
        return status;
    }

    private String normalizeNullableTagType(String type) {
        if (!StringUtils.hasText(type)) {
            return null;
        }
        return normalizeRequiredTagType(type);
    }

    private String normalizeRequiredTagType(String type) {
        String normalizedType = type.trim().toUpperCase();
        if (!TAG_TYPES.contains(normalizedType)) {
            throw new RuntimeException("标签类型不支持");
        }
        return normalizedType;
    }

    private PageQuery normalizePage(Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        return new PageQuery(safePageSize, (safePageNum - 1) * safePageSize);
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private CourseCategoryVO toCategoryVO(CourseCategory category) {
        CourseCategoryVO vo = new CourseCategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setType(category.getType());
        vo.setSortNo(category.getSortNo());
        return vo;
    }

    private TagVO toTagVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setType(tag.getType());
        return vo;
    }

    private record PageQuery(int pageSize, int offset) {
    }
}
