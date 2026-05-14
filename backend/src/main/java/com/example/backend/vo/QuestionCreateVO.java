package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionCreateVO {

    private Long questionId;
    private String auditStatus;
    private String auditResult;
}
