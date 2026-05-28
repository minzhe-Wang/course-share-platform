package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecommendationItemVO {

    private String targetType;
    private Long targetId;
    private String title;
    private String description;
    private String categoryName;
    private Long score;
    private String reason;
    private LocalDateTime createTime;
}
