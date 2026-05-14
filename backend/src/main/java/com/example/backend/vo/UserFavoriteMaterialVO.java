package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFavoriteMaterialVO {

    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private String fileType;
    private Long fileSize;
    private String uploaderName;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private LocalDateTime favoriteTime;
}
