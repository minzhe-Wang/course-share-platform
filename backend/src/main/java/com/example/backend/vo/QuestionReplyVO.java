package com.example.backend.vo;

import lombok.Data;

@Data
public class QuestionReplyVO {

    private Long id;
    private Long userId;
    private String content;
    private String userName;
    private String replyToUserName;
    private Integer likeCount;
}
