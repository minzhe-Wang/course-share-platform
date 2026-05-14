package com.example.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDetailVO {

    private Long id;
    private String title;
    private String content;
    private String categoryName;
    private String userName;
    private Integer viewCount;
    private Integer likeCount;
    private List<QuestionAnswerVO> answers;
}
