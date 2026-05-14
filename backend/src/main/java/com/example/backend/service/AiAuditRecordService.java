package com.example.backend.service;

import com.example.backend.vo.AiAuditRecordDetailVO;
import com.example.backend.vo.AiAuditRecordListItemVO;
import com.example.backend.vo.PageResultVO;

public interface AiAuditRecordService {

    PageResultVO<AiAuditRecordListItemVO> listRecords(Integer pageNum, Integer pageSize,
                                                      String targetType, String auditResult,
                                                      String authorization);

    AiAuditRecordDetailVO getRecordDetail(Long id, String authorization);
}
