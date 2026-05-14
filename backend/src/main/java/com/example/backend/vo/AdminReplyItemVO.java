package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminReplyItemVO {

    private Long id;
    private Long answerId;
    private Long questionId;
    private String questionTitle;
    private String content;
    private String userName;
    private String replyToUserName;
    private String auditStatus;
    private String auditRemark;
    private Integer status;
    private Integer likeCount;
    private LocalDateTime createTime;
}
