package com.example.backend.service.impl;

import com.example.backend.dto.MaterialCreateDTO;
import com.example.backend.entity.AiAuditRecord;
import com.example.backend.entity.CourseCategory;
import com.example.backend.entity.Material;
import com.example.backend.entity.SysUser;
import com.example.backend.entity.Tag;
import com.example.backend.mapper.AiAuditRecordMapper;
import com.example.backend.mapper.CourseCategoryMapper;
import com.example.backend.mapper.MaterialMapper;
import com.example.backend.mapper.TagMapper;
import com.example.backend.service.AiAuditService;
import com.example.backend.service.AuthService;
import com.example.backend.service.CacheService;
import com.example.backend.service.MaterialService;
import com.example.backend.vo.AiAuditResultVO;
import com.example.backend.vo.MaterialCreateVO;
import com.example.backend.vo.MaterialDetailVO;
import com.example.backend.vo.MaterialDownloadVO;
import com.example.backend.vo.MaterialListItemVO;
import com.example.backend.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private static final int NORMAL_STATUS = 1;
    private static final Set<String> ALLOWED_FILE_TYPES = Set.of("PDF", "DOC", "DOCX", "ZIP");

    private final MaterialMapper materialMapper;
    private final CourseCategoryMapper courseCategoryMapper;
    private final TagMapper tagMapper;
    private final AiAuditService aiAuditService;
    private final AiAuditRecordMapper aiAuditRecordMapper;
    private final AuthService authService;
    private final CacheService cacheService;

    @Override
    @Transactional
    public MaterialCreateVO createMaterial(MaterialCreateDTO materialCreateDTO, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作资料");
        CourseCategory category = getEnabledCategory(materialCreateDTO.getCategoryId());
        List<Long> tagIds = normalizeTagIds(materialCreateDTO.getTagIds());
        checkTags(tagIds);
        String fileType = normalizeFileType(materialCreateDTO.getFileType());

        Material material = Material.builder()
                .title(materialCreateDTO.getTitle().trim())
                .description(trimToNull(materialCreateDTO.getDescription()))
                .categoryId(category.getId())
                .fileUrl(materialCreateDTO.getFileUrl().trim())
                .fileKey(materialCreateDTO.getFileKey().trim())
                .originalFilename(materialCreateDTO.getOriginalFilename().trim())
                .fileType(fileType)
                .fileSize(materialCreateDTO.getFileSize())
                .uploaderId(user.getId())
                .auditStatus("PENDING")
                .status(NORMAL_STATUS)
                .build();
        materialMapper.insert(material);

        for (Long tagId : tagIds) {
            materialMapper.insertMaterialTag(material.getId(), tagId);
        }

        String auditContent = buildAuditContent(materialCreateDTO);
        AiAuditResultVO auditResult = aiAuditService.audit("MATERIAL", auditContent);
        String auditStatus = "PASS".equals(auditResult.getAuditResult()) ? "APPROVED" : "REJECTED";

        aiAuditRecordMapper.insert(AiAuditRecord.builder()
                .targetType("MATERIAL")
                .targetId(material.getId())
                .auditResult(auditResult.getAuditResult())
                .riskScore(auditResult.getRiskScore())
                .reason(auditResult.getReason())
                .modelName("mock-ai-audit")
                .requestContent(auditContent)
                .responseContent(auditResult.getReason())
                .build());

        materialMapper.updateAuditResult(material.getId(), auditStatus, auditResult.getReason(), LocalDateTime.now());
        if ("APPROVED".equals(auditStatus)) {
            cacheService.evictHotMaterials();
        }

        return MaterialCreateVO.builder()
                .materialId(material.getId())
                .auditStatus(auditStatus)
                .auditResult(auditResult.getAuditResult())
                .build();
    }

    @Override
    public PageResultVO<MaterialListItemVO> listMaterials(Integer pageNum, Integer pageSize, String keyword,
                                                          Long categoryId, Long tagId, String sortBy) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int offset = (safePageNum - 1) * safePageSize;
        String normalizedKeyword = trimToNull(keyword);

        Long total = materialMapper.countApprovedMaterials(normalizedKeyword, categoryId, tagId);
        List<MaterialListItemVO> list = materialMapper.findApprovedMaterials(
                normalizedKeyword,
                categoryId,
                tagId,
                resolveOrderBy(sortBy),
                safePageSize,
                offset
        );

        return PageResultVO.<MaterialListItemVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public MaterialDetailVO getMaterialDetail(Long id) {
        MaterialDetailVO detail = getApprovedMaterialDetail(id);
        materialMapper.incrementViewCount(id);
        cacheService.evictHotMaterials();
        detail.setViewCount(detail.getViewCount() == null ? 1 : detail.getViewCount() + 1);
        detail.setTags(tagMapper.findByMaterialId(id));
        return detail;
    }

    @Override
    @Transactional
    public MaterialDownloadVO downloadMaterial(Long id, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作资料");
        MaterialDetailVO detail = getApprovedMaterialDetail(id);

        materialMapper.insertDownloadRecord(user.getId(), id, null);
        materialMapper.incrementDownloadCount(id);
        cacheService.evictHotMaterials();

        return MaterialDownloadVO.builder()
                .downloadUrl(detail.getFileUrl())
                .build();
    }

    @Override
    @Transactional
    public void likeMaterial(Long id, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作资料");
        getApprovedMaterialDetail(id);

        if (materialMapper.countMaterialLike(user.getId(), id) > 0) {
            throw new RuntimeException("已经点赞");
        }

        materialMapper.insertMaterialLike(user.getId(), id);
        materialMapper.incrementLikeCount(id);
        cacheService.evictHotMaterials();
    }

    @Override
    @Transactional
    public void favoriteMaterial(Long id, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作资料");
        getApprovedMaterialDetail(id);

        if (materialMapper.countMaterialFavorite(user.getId(), id) > 0) {
            throw new RuntimeException("已经收藏");
        }

        materialMapper.insertMaterialFavorite(user.getId(), id);
        materialMapper.incrementFavoriteCount(id);
        cacheService.evictHotMaterials();
    }

    @Override
    @Transactional
    public void cancelFavoriteMaterial(Long id, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作资料");
        getApprovedMaterialDetail(id);

        if (materialMapper.countMaterialFavorite(user.getId(), id) == 0) {
            throw new RuntimeException("尚未收藏");
        }

        materialMapper.deleteMaterialFavorite(user.getId(), id);
        materialMapper.decrementFavoriteCount(id);
        cacheService.evictHotMaterials();
    }

    private MaterialDetailVO getApprovedMaterialDetail(Long id) {
        if (id == null) {
            throw new RuntimeException("资料不存在");
        }

        MaterialDetailVO detail = materialMapper.findApprovedDetailById(id);
        if (detail == null) {
            throw new RuntimeException("资料不存在");
        }
        return detail;
    }

    private CourseCategory getEnabledCategory(Long categoryId) {
        CourseCategory category = courseCategoryMapper.findById(categoryId);
        if (category == null || category.getStatus() == null || category.getStatus() != NORMAL_STATUS) {
            throw new RuntimeException("课程分类不存在");
        }
        return category;
    }

    private void checkTags(List<Long> tagIds) {
        if (tagIds.isEmpty()) {
            return;
        }

        List<Tag> tags = tagMapper.findByIds(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new RuntimeException("标签不存在");
        }
        for (Tag tag : tags) {
            if (tag.getStatus() == null || tag.getStatus() != NORMAL_STATUS) {
                throw new RuntimeException("标签不存在");
            }
        }
    }

    private List<Long> normalizeTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }

        Set<Long> uniqueTagIds = new HashSet<>();
        for (Long tagId : tagIds) {
            if (tagId == null) {
                throw new RuntimeException("标签不存在");
            }
            uniqueTagIds.add(tagId);
        }
        return uniqueTagIds.stream().toList();
    }

    private String normalizeFileType(String fileType) {
        String normalizedFileType = fileType.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_FILE_TYPES.contains(normalizedFileType)) {
            throw new RuntimeException("文件类型不支持");
        }
        return normalizedFileType;
    }

    private String buildAuditContent(MaterialCreateDTO materialCreateDTO) {
        return materialCreateDTO.getTitle() + "\n"
                + (materialCreateDTO.getDescription() == null ? "" : materialCreateDTO.getDescription()) + "\n"
                + materialCreateDTO.getOriginalFilename();
    }

    private String resolveOrderBy(String sortBy) {
        if ("like".equals(sortBy)) {
            return "m.like_count DESC, m.create_time DESC";
        }
        if ("favorite".equals(sortBy)) {
            return "m.favorite_count DESC, m.create_time DESC";
        }
        if ("download".equals(sortBy)) {
            return "m.download_count DESC, m.create_time DESC";
        }
        return "m.create_time DESC";
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
