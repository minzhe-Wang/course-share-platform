package com.example.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class QuestionAnswerVO {

    private Long id;
    private String content;
    private String userName;
    private Integer likeCount;
    private Integer replyCount;
    private List<QuestionReplyVO> replies;
}
