package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerCreateVO {

    private Long answerId;
    private String auditStatus;
    private String auditResult;
}
