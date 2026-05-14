package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminAnswerItemVO {

    private Long id;
    private Long questionId;
    private String questionTitle;
    private String content;
    private String userName;
    private String auditStatus;
    private String auditRemark;
    private Integer status;
    private Integer likeCount;
    private Integer replyCount;
    private LocalDateTime createTime;
}
