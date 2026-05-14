package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminMaterialItemVO {

    private Long id;
    private String title;
    private String categoryName;
    private String uploaderName;
    private String fileType;
    private String auditStatus;
    private String auditRemark;
    private Integer status;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private LocalDateTime createTime;
}
