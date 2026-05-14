package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminQuestionItemVO {

    private Long id;
    private String title;
    private String categoryName;
    private String userName;
    private String auditStatus;
    private String auditRemark;
    private Integer status;
    private Integer viewCount;
    private Integer answerCount;
    private Integer likeCount;
    private LocalDateTime createTime;
}
