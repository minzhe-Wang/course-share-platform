package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthComponentVO {

    private String name;
    private String status;
    private String message;
}
