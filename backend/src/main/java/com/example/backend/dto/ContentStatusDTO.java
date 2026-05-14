package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentStatusDTO {

    @NotNull(message = "内容状态不能为空")
    private Integer status;
}
