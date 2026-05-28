package com.example.backend.service.impl;

import com.example.backend.dto.AnswerCreateDTO;
import com.example.backend.dto.AnswerReplyCreateDTO;
import com.example.backend.dto.QuestionCreateDTO;
import com.example.backend.entity.AiAuditRecord;
import com.example.backend.entity.Answer;
import com.example.backend.entity.AnswerReply;
import com.example.backend.entity.CourseCategory;
import com.example.backend.entity.Question;
import com.example.backend.entity.SysUser;
import com.example.backend.mapper.AiAuditRecordMapper;
import com.example.backend.mapper.CourseCategoryMapper;
import com.example.backend.mapper.QuestionMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.AiAuditService;
import com.example.backend.service.AuthService;
import com.example.backend.service.CacheService;
import com.example.backend.service.QuestionService;
import com.example.backend.vo.AiAuditResultVO;
import com.example.backend.vo.AnswerCreateVO;
import com.example.backend.vo.AnswerReplyCreateVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.QuestionAnswerVO;
import com.example.backend.vo.QuestionCreateVO;
import com.example.backend.vo.QuestionDetailVO;
import com.example.backend.vo.QuestionListItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private static final int NORMAL_STATUS = 1;

    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final CourseCategoryMapper courseCategoryMapper;
    private final AiAuditService aiAuditService;
    private final AiAuditRecordMapper aiAuditRecordMapper;
    private final AuthService authService;
    private final CacheService cacheService;

    @Override
    @Transactional
    public QuestionCreateVO createQuestion(QuestionCreateDTO questionCreateDTO, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作问答");
        CourseCategory category = getEnabledCategory(questionCreateDTO.getCategoryId());

        Question question = Question.builder()
                .title(questionCreateDTO.getTitle().trim())
                .content(questionCreateDTO.getContent().trim())
                .categoryId(category.getId())
                .userId(user.getId())
                .auditStatus("PENDING")
                .status(NORMAL_STATUS)
                .build();
        questionMapper.insertQuestion(question);

        AiAuditResultVO auditResult = aiAuditService.audit("QUESTION", question.getTitle() + "\n" + question.getContent());
        String auditStatus = resolveAuditStatus(auditResult);
        insertAuditRecord("QUESTION", question.getId(), auditResult, question.getTitle() + "\n" + question.getContent());
        questionMapper.updateQuestionAudit(question.getId(), auditStatus, auditResult.getReason(), LocalDateTime.now());
        if ("APPROVED".equals(auditStatus)) {
            cacheService.evictHotQuestions();
        }

        return QuestionCreateVO.builder()
                .questionId(question.getId())
                .auditStatus(auditStatus)
                .auditResult(auditResult.getAuditResult())
                .build();
    }

    @Override
    public PageResultVO<QuestionListItemVO> listQuestions(Integer pageNum, Integer pageSize, String keyword,
                                                          Long categoryId, String sortBy) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int offset = (safePageNum - 1) * safePageSize;
        String normalizedKeyword = trimToNull(keyword);

        Long total = questionMapper.countApprovedQuestions(normalizedKeyword, categoryId);
        List<QuestionListItemVO> list = questionMapper.findApprovedQuestions(
                normalizedKeyword,
                categoryId,
                resolveOrderBy(sortBy),
                safePageSize,
                offset
        );

        return PageResultVO.<QuestionListItemVO>builder()
                .total(total)
                .list(list)
                .build();
    }

    @Override
    public QuestionDetailVO getQuestionDetail(Long id) {
        QuestionDetailVO detail = getApprovedQuestionDetail(id);
        questionMapper.incrementQuestionViewCount(id);
        cacheService.evictHotQuestions();
        detail.setViewCount(detail.getViewCount() == null ? 1 : detail.getViewCount() + 1);

        List<QuestionAnswerVO> answers = questionMapper.findApprovedAnswers(id);
        for (QuestionAnswerVO answer : answers) {
            answer.setReplies(questionMapper.findApprovedReplies(answer.getId()));
        }
        detail.setAnswers(answers);
        return detail;
    }

    @Override
    @Transactional
    public AnswerCreateVO createAnswer(Long questionId, AnswerCreateDTO answerCreateDTO, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作问答");
        getApprovedQuestionDetail(questionId);

        Answer answer = Answer.builder()
                .questionId(questionId)
                .content(answerCreateDTO.getContent().trim())
                .userId(user.getId())
                .auditStatus("PENDING")
                .status(NORMAL_STATUS)
                .build();
        questionMapper.insertAnswer(answer);

        AiAuditResultVO auditResult = aiAuditService.audit("ANSWER", answer.getContent());
        String auditStatus = resolveAuditStatus(auditResult);
        insertAuditRecord("ANSWER", answer.getId(), auditResult, answer.getContent());
        questionMapper.updateAnswerAudit(answer.getId(), auditStatus, auditResult.getReason(), LocalDateTime.now());
        if ("APPROVED".equals(auditStatus)) {
            questionMapper.incrementAnswerCount(questionId);
            cacheService.evictHotQuestions();
        }

        return AnswerCreateVO.builder()
                .answerId(answer.getId())
                .auditStatus(auditStatus)
                .auditResult(auditResult.getAuditResult())
                .build();
    }

    @Override
    @Transactional
    public AnswerReplyCreateVO createReply(Long answerId, AnswerReplyCreateDTO answerReplyCreateDTO, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作问答");
        Answer answer = questionMapper.findAnswerById(answerId);
        if (answer == null || answer.getStatus() == null || answer.getStatus() != NORMAL_STATUS
                || !"APPROVED".equals(answer.getAuditStatus())) {
            throw new RuntimeException("回答不存在");
        }
        validateReplyToUser(answerId, answerReplyCreateDTO.getReplyToUserId());

        AnswerReply reply = AnswerReply.builder()
                .answerId(answerId)
                .userId(user.getId())
                .replyToUserId(answerReplyCreateDTO.getReplyToUserId())
                .content(answerReplyCreateDTO.getContent().trim())
                .auditStatus("PENDING")
                .status(NORMAL_STATUS)
                .build();
        questionMapper.insertReply(reply);

        AiAuditResultVO auditResult = aiAuditService.audit("REPLY", reply.getContent());
        String auditStatus = resolveAuditStatus(auditResult);
        insertAuditRecord("REPLY", reply.getId(), auditResult, reply.getContent());
        questionMapper.updateReplyAudit(reply.getId(), auditStatus, auditResult.getReason(), LocalDateTime.now());
        if ("APPROVED".equals(auditStatus)) {
            questionMapper.incrementReplyCount(answerId);
            cacheService.evictHotQuestions();
        }

        return AnswerReplyCreateVO.builder()
                .replyId(reply.getId())
                .auditStatus(auditStatus)
                .auditResult(auditResult.getAuditResult())
                .build();
    }

    @Override
    @Transactional
    public void likeQuestion(Long id, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作问答");
        getApprovedQuestionDetail(id);
        likeTarget(user.getId(), "QUESTION", id);
        questionMapper.incrementQuestionLikeCount(id);
        cacheService.evictHotQuestions();
    }

    @Override
    @Transactional
    public void likeAnswer(Long id, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作问答");
        Answer answer = questionMapper.findAnswerById(id);
        if (answer == null || answer.getStatus() == null || answer.getStatus() != NORMAL_STATUS
                || !"APPROVED".equals(answer.getAuditStatus())) {
            throw new RuntimeException("回答不存在");
        }
        likeTarget(user.getId(), "ANSWER", id);
        questionMapper.incrementAnswerLikeCount(id);
        cacheService.evictHotQuestions();
    }

    @Override
    @Transactional
    public void likeReply(Long id, String authorization) {
        SysUser user = authService.getEnabledStudent(authorization, "只有学生可以操作问答");
        if (questionMapper.findApprovedReplyId(id) == null) {
            throw new RuntimeException("回复不存在");
        }
        likeTarget(user.getId(), "REPLY", id);
        questionMapper.incrementReplyLikeCount(id);
        cacheService.evictHotQuestions();
    }

    private QuestionDetailVO getApprovedQuestionDetail(Long id) {
        if (id == null) {
            throw new RuntimeException("问题不存在");
        }
        QuestionDetailVO detail = questionMapper.findApprovedQuestionDetail(id);
        if (detail == null) {
            throw new RuntimeException("问题不存在");
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

    private void validateReplyToUser(Long answerId, Long replyToUserId) {
        if (replyToUserId == null) {
            return;
        }
        Answer answer = questionMapper.findAnswerById(answerId);
        if (answer != null && replyToUserId.equals(answer.getUserId())) {
            return;
        }
        SysUser replyToUser = userMapper.findById(replyToUserId);
        if (replyToUser == null) {
            throw new RuntimeException("被回复用户不存在");
        }
        if (questionMapper.countReplyUserInAnswer(answerId, replyToUserId) == 0) {
            throw new RuntimeException("被回复用户不在该回答的回复列表中");
        }
    }

    private void insertAuditRecord(String targetType, Long targetId, AiAuditResultVO auditResult, String requestContent) {
        aiAuditRecordMapper.insert(AiAuditRecord.builder()
                .targetType(targetType)
                .targetId(targetId)
                .auditResult(auditResult.getAuditResult())
                .riskScore(auditResult.getRiskScore())
                .reason(auditResult.getReason())
                .modelName("lexicon-audit")
                .requestContent(requestContent)
                .responseContent(auditResult.getReason())
                .build());
    }

    private void likeTarget(Long userId, String targetType, Long targetId) {
        if (questionMapper.countLikeRecord(userId, targetType, targetId) > 0) {
            throw new RuntimeException("已经点赞");
        }
        questionMapper.insertLikeRecord(userId, targetType, targetId);
    }

    private String resolveAuditStatus(AiAuditResultVO auditResult) {
        return "PASS".equals(auditResult.getAuditResult()) ? "APPROVED" : "REJECTED";
    }

    private String resolveOrderBy(String sortBy) {
        if ("hot".equals(sortBy)) {
            return "q.like_count DESC, q.answer_count DESC, q.create_time DESC";
        }
        return "q.create_time DESC";
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
