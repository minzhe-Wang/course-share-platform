package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MaterialDetailVO {

    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private List<TagVO> tags;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String uploaderName;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private LocalDateTime createTime;
}
