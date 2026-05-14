package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.AnswerCreateDTO;
import com.example.backend.dto.AnswerReplyCreateDTO;
import com.example.backend.dto.QuestionCreateDTO;
import com.example.backend.service.QuestionService;
import com.example.backend.vo.AnswerCreateVO;
import com.example.backend.vo.AnswerReplyCreateVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.QuestionCreateVO;
import com.example.backend.vo.QuestionDetailVO;
import com.example.backend.vo.QuestionListItemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/api/questions")
    public Result<QuestionCreateVO> createQuestion(@RequestBody @Valid QuestionCreateDTO questionCreateDTO,
                                                   @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(questionService.createQuestion(questionCreateDTO, authorization));
    }

    @GetMapping("/api/questions")
    public Result<PageResultVO<QuestionListItemVO>> listQuestions(@RequestParam(required = false) Integer pageNum,
                                                                  @RequestParam(required = false) Integer pageSize,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) Long categoryId,
                                                                  @RequestParam(required = false) String sortBy) {
        return Result.success(questionService.listQuestions(pageNum, pageSize, keyword, categoryId, sortBy));
    }

    @GetMapping("/api/questions/{id}")
    public Result<QuestionDetailVO> getQuestionDetail(@PathVariable Long id) {
        return Result.success(questionService.getQuestionDetail(id));
    }

    @PostMapping("/api/questions/{id}/answers")
    public Result<AnswerCreateVO> createAnswer(@PathVariable Long id,
                                               @RequestBody @Valid AnswerCreateDTO answerCreateDTO,
                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(questionService.createAnswer(id, answerCreateDTO, authorization));
    }

    @PostMapping("/api/answers/{id}/replies")
    public Result<AnswerReplyCreateVO> createReply(@PathVariable Long id,
                                                   @RequestBody @Valid AnswerReplyCreateDTO answerReplyCreateDTO,
                                                   @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(questionService.createReply(id, answerReplyCreateDTO, authorization));
    }

    @PostMapping("/api/questions/{id}/like")
    public Result<Void> likeQuestion(@PathVariable Long id,
                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        questionService.likeQuestion(id, authorization);
        return Result.success();
    }

    @PostMapping("/api/answers/{id}/like")
    public Result<Void> likeAnswer(@PathVariable Long id,
                                   @RequestHeader(value = "Authorization", required = false) String authorization) {
        questionService.likeAnswer(id, authorization);
        return Result.success();
    }

    @PostMapping("/api/replies/{id}/like")
    public Result<Void> likeReply(@PathVariable Long id,
                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        questionService.likeReply(id, authorization);
        return Result.success();
    }
}
