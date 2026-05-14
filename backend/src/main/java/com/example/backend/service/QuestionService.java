package com.example.backend.service;

import com.example.backend.dto.AnswerCreateDTO;
import com.example.backend.dto.AnswerReplyCreateDTO;
import com.example.backend.dto.QuestionCreateDTO;
import com.example.backend.vo.AnswerCreateVO;
import com.example.backend.vo.AnswerReplyCreateVO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.QuestionCreateVO;
import com.example.backend.vo.QuestionDetailVO;
import com.example.backend.vo.QuestionListItemVO;

public interface QuestionService {

    QuestionCreateVO createQuestion(QuestionCreateDTO questionCreateDTO, String authorization);

    PageResultVO<QuestionListItemVO> listQuestions(Integer pageNum, Integer pageSize, String keyword,
                                                   Long categoryId, String sortBy);

    QuestionDetailVO getQuestionDetail(Long id);

    AnswerCreateVO createAnswer(Long questionId, AnswerCreateDTO answerCreateDTO, String authorization);

    AnswerReplyCreateVO createReply(Long answerId, AnswerReplyCreateDTO answerReplyCreateDTO, String authorization);

    void likeQuestion(Long id, String authorization);

    void likeAnswer(Long id, String authorization);

    void likeReply(Long id, String authorization);
}
