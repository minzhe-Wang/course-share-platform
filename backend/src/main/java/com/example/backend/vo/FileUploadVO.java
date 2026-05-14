package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadVO {

    private String originalFilename;
    private String fileKey;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
}
