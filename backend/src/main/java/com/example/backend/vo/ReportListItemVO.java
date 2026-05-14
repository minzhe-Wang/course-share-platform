package com.example.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportListItemVO {

    private Long id;
    private String targetType;
    private Long targetId;
    private String targetSnapshot;
    private String reportUserName;
    private String reason;
    private String handleStatus;
    private LocalDateTime createTime;
}
