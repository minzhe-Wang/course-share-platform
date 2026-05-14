package com.example.backend.service;

import com.example.backend.vo.AiAuditResultVO;

public interface AiAuditService {

    AiAuditResultVO audit(String targetType, String content);
}
