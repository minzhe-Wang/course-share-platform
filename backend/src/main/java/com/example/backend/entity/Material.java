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
public class Material {

    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String fileUrl;
    private String fileKey;
    private String originalFilename;
    private String fileType;
    private Long fileSize;
    private Long uploaderId;
    private String auditStatus;
    private Long auditorId;
    private String auditRemark;
    private LocalDateTime auditTime;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer isTop;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
