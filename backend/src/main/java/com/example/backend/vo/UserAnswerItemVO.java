package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAnswerItemVO {

    private Long id;
    private Long questionId;
    private String questionTitle;
    private String content;
    private String auditStatus;
    private String auditRemark;
    private Integer likeCount;
    private Integer replyCount;
    private LocalDateTime createTime;
}
