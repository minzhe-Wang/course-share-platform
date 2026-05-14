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
public class Answer {

    private Long id;
    private Long questionId;
    private String content;
    private Long userId;
    private String auditStatus;
    private Long auditorId;
    private String auditRemark;
    private LocalDateTime auditTime;
    private Integer likeCount;
    private Integer replyCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
