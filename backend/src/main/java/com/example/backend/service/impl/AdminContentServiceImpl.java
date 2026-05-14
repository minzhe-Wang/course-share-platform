package com.example.backend.service.impl;

import com.example.backend.dto.ContentStatusDTO;
import com.example.backend.entity.Answer;
import com.example.backend.entity.AnswerReply;
import com.example.backend.entity.Material;
import com.example.backend.entity.Question;
import com.example.backend.mapper.MaterialMapper;
import com.example.backend.mapper.QuestionMapper;
import com.example.backend.service.AdminContentService;
import com.example.backend.service.AuthService;
import com.example.backend.service.CacheService;
import com.example.backend.vo.AdminAnswerItemVO;
import com.example.backend.vo.AdminMaterialItemVO;
import com.example.backend.vo.AdminQuestionItemVO;
import com.example.backend.vo.AdminReplyItemVO;
import com.example.backend.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminContentServiceImpl implements AdminContentService {

    private static final Set<String> AUDIT_STATUSES = Set.of("PENDING", "APPROVED", "REJECTED");

    private final MaterialMapper materialMapper;
    private final QuestionMapper questionMapper;
    private final AuthService authService;
    private final CacheService cacheService;

    @Override
    public PageResultVO<AdminMaterialItemVO> listMaterials(Integer pageNum, Integer pageSize, String keyword,
                                                           String auditStatus, Integer status, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限管理内容");
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        String normalizedKeyword = trimToNull(keyword);
        String normalizedAuditStatus = normalizeNullableAuditStatus(auditStatus);
        Integer normalizedStatus = normalizeNullableStatus(status);
        Long total = materialMapper.countAdminMaterials(normalizedKeyword, normalizedAuditStatus, normalizedStatus);
        List<AdminMaterialItemVO> list = materialMapper.findAdminMaterials(
                normalizedKeyword,
                normalizedAuditStatus,
                normalizedStatus,
                pageQuery.pageSize(),
                pageQuery.offset()
        );
        return PageResultVO.<AdminMaterialItemVO>builder().total(total).list(list).build();
    }

    @Override
    public void updateMaterialStatus(Long id, ContentStatusDTO contentStatusDTO, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限管理内容");
        Material material = materialMapper.findById(id);
        if (material == null) {
            throw new RuntimeException("资料不存在");
        }
        materialMapper.updateStatus(id, normalizeRequiredStatus(contentStatusDTO.getStatus()));
        cacheService.evictHotMaterials();
    }

    @Override
    public PageResultVO<AdminQuestionItemVO> listQuestions(Integer pageNum, Integer pageSize, String keyword,
                                                           String auditStatus, Integer status, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限管理内容");
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        String normalizedKeyword = trimToNull(keyword);
        String normalizedAuditStatus = normalizeNullableAuditStatus(auditStatus);
        Integer normalizedStatus = normalizeNullableStatus(status);
        Long total = questionMapper.countAdminQuestions(normalizedKeyword, normalizedAuditStatus, normalizedStatus);
        List<AdminQuestionItemVO> list = questionMapper.findAdminQuestions(
                normalizedKeyword,
                normalizedAuditStatus,
                normalizedStatus,
                pageQuery.pageSize(),
                pageQuery.offset()
        );
        return PageResultVO.<AdminQuestionItemVO>builder().total(total).list(list).build();
    }

    @Override
    public void updateQuestionStatus(Long id, ContentStatusDTO contentStatusDTO, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限管理内容");
        Question question = questionMapper.findQuestionById(id);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        questionMapper.updateQuestionStatus(id, normalizeRequiredStatus(contentStatusDTO.getStatus()));
        cacheService.evictHotQuestions();
    }

    @Override
    public PageResultVO<AdminAnswerItemVO> listAnswers(Integer pageNum, Integer pageSize, String keyword,
                                                       String auditStatus, Integer status, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限管理内容");
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        String normalizedKeyword = trimToNull(keyword);
        String normalizedAuditStatus = normalizeNullableAuditStatus(auditStatus);
        Integer normalizedStatus = normalizeNullableStatus(status);
        Long total = questionMapper.countAdminAnswers(normalizedKeyword, normalizedAuditStatus, normalizedStatus);
        List<AdminAnswerItemVO> list = questionMapper.findAdminAnswers(
                normalizedKeyword,
                normalizedAuditStatus,
                normalizedStatus,
                pageQuery.pageSize(),
                pageQuery.offset()
        );
        return PageResultVO.<AdminAnswerItemVO>builder().total(total).list(list).build();
    }

    @Override
    public void updateAnswerStatus(Long id, ContentStatusDTO contentStatusDTO, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限管理内容");
        Answer answer = questionMapper.findAnswerById(id);
        if (answer == null) {
            throw new RuntimeException("回答不存在");
        }
        questionMapper.updateAnswerStatus(id, normalizeRequiredStatus(contentStatusDTO.getStatus()));
        cacheService.evictHotQuestions();
    }

    @Override
    public PageResultVO<AdminReplyItemVO> listReplies(Integer pageNum, Integer pageSize, String keyword,
                                                      String auditStatus, Integer status, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限管理内容");
        PageQuery pageQuery = normalizePage(pageNum, pageSize);
        String normalizedKeyword = trimToNull(keyword);
        String normalizedAuditStatus = normalizeNullableAuditStatus(auditStatus);
        Integer normalizedStatus = normalizeNullableStatus(status);
        Long total = questionMapper.countAdminReplies(normalizedKeyword, normalizedAuditStatus, normalizedStatus);
        List<AdminReplyItemVO> list = questionMapper.findAdminReplies(
                normalizedKeyword,
                normalizedAuditStatus,
                normalizedStatus,
                pageQuery.pageSize(),
                pageQuery.offset()
        );
        return PageResultVO.<AdminReplyItemVO>builder().total(total).list(list).build();
    }

    @Override
    public void updateReplyStatus(Long id, ContentStatusDTO contentStatusDTO, String authorization) {
        authService.getEnabledReviewerOrAdmin(authorization, "无权限管理内容");
        AnswerReply reply = questionMapper.findReplyById(id);
        if (reply == null) {
            throw new RuntimeException("回复不存在");
        }
        questionMapper.updateReplyStatus(id, normalizeRequiredStatus(contentStatusDTO.getStatus()));
        cacheService.evictHotQuestions();
    }

    private String normalizeNullableAuditStatus(String auditStatus) {
        if (!StringUtils.hasText(auditStatus)) {
            return null;
        }
        String normalizedStatus = auditStatus.trim().toUpperCase();
        if (!AUDIT_STATUSES.contains(normalizedStatus)) {
            throw new RuntimeException("审核状态不支持");
        }
        return normalizedStatus;
    }

    private Integer normalizeNullableStatus(Integer status) {
        if (status == null) {
            return null;
        }
        return normalizeRequiredStatus(status);
    }

    private Integer normalizeRequiredStatus(Integer status) {
        if (!Integer.valueOf(0).equals(status) && !Integer.valueOf(1).equals(status)) {
            throw new RuntimeException("内容状态不支持");
        }
        return status;
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

    private record PageQuery(int pageSize, int offset) {
    }
}
