package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BasicStatusDTO {

    @NotNull(message = "状态不能为空")
    private Integer status;
}
