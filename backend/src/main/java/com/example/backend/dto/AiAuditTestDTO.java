package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiAuditTestDTO {

    @NotBlank(message = "审核对象类型不能为空")
    private String targetType;

    @NotBlank(message = "审核内容不能为空")
    private String content;
}
