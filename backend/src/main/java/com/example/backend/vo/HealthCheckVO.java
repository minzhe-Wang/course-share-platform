package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HealthCheckVO {

    private String status;
    private LocalDateTime checkTime;
    private List<HealthComponentVO> components;
}
