package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerReplyCreateDTO {

    @NotBlank(message = "回复内容不能为空")
    private String content;

    private Long replyToUserId;
}
