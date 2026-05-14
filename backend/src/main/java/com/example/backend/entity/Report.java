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
public class Report {

    private Long id;
    private String targetType;
    private Long targetId;
    private String targetSnapshot;
    private Long reportUserId;
    private String reason;
    private String handleStatus;
    private Long handleUserId;
    private String handleResult;
    private LocalDateTime handleTime;
    private LocalDateTime createTime;
}
