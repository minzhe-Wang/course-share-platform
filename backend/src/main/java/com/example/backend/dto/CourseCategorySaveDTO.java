package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseCategorySaveDTO {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50位")
    private String name;

    @NotBlank(message = "分类类型不能为空")
    @Size(max = 50, message = "分类类型长度不能超过50位")
    private String type;

    @NotNull(message = "排序号不能为空")
    private Integer sortNo;
}
