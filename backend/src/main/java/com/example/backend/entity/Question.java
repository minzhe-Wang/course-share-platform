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
public class Question {

    private Long id;
    private String title;
    private String content;
    private Long categoryId;
    private Long userId;
    private String auditStatus;
    private Long auditorId;
    private String auditRemark;
    private LocalDateTime auditTime;
    private Integer viewCount;
    private Integer answerCount;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
