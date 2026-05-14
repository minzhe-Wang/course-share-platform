package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MaterialCreateDTO {

    @NotBlank(message = "资料标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "课程分类不能为空")
    private Long categoryId;

    private List<Long> tagIds;

    @NotBlank(message = "文件访问地址不能为空")
    private String fileUrl;

    @NotBlank(message = "文件对象Key不能为空")
    private String fileKey;

    @NotBlank(message = "原始文件名不能为空")
    private String originalFilename;

    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    @NotNull(message = "文件大小不能为空")
    @Min(value = 1, message = "文件大小必须大于0")
    private Long fileSize;
}
