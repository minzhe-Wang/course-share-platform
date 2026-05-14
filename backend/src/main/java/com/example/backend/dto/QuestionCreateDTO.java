package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionCreateDTO {

    @NotBlank(message = "问题标题不能为空")
    private String title;

    @NotBlank(message = "问题内容不能为空")
    private String content;

    @NotNull(message = "课程分类不能为空")
    private Long categoryId;
}
