package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQuestionItemVO {

    private Long id;
    private String title;
    private String content;
    private String categoryName;
    private String auditStatus;
    private String auditRemark;
    private Integer viewCount;
    private Integer answerCount;
    private Integer likeCount;
    private LocalDateTime createTime;
}
