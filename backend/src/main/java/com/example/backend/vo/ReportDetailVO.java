package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportDetailVO {

    private Long id;
    private String targetType;
    private Long targetId;
    private String targetSnapshot;
    private Long reportUserId;
    private String reportUserName;
    private String reason;
    private String handleStatus;
    private Long handleUserId;
    private String handleUserName;
    private String handleResult;
    private LocalDateTime handleTime;
    private LocalDateTime createTime;
}
