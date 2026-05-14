package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportHandleDTO {

    @NotBlank(message = "处理状态不能为空")
    private String handleStatus;

    @NotBlank(message = "处理说明不能为空")
    private String handleResult;
}
