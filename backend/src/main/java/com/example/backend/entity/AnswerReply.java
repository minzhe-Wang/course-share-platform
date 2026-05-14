package com.example.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerReply {

    private Long id;
    private Long answerId;
    private Long userId;
    private Long replyToUserId;
    private String content;
    private String auditStatus;
    private Long auditorId;
    private String auditRemark;
    private LocalDateTime auditTime;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
