package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDownloadRecordVO {

    private Long materialId;
    private String title;
    private String originalFilename;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private LocalDateTime downloadTime;
}
