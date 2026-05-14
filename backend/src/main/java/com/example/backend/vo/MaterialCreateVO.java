package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialCreateVO {

    private Long materialId;
    private String auditStatus;
    private String auditResult;
}
