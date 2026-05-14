package com.example.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerReplyCreateVO {

    private Long replyId;
    private String auditStatus;
    private String auditResult;
}
