package com.example.backend.service;

import com.example.backend.dto.ReportCreateDTO;
import com.example.backend.dto.ReportHandleDTO;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.ReportCreateVO;
import com.example.backend.vo.ReportDetailVO;
import com.example.backend.vo.ReportListItemVO;

public interface ReportService {

    ReportCreateVO createReport(ReportCreateDTO reportCreateDTO, String authorization);

    PageResultVO<ReportListItemVO> listReports(Integer pageNum, Integer pageSize, String handleStatus, String authorization);

    ReportDetailVO getReportDetail(Long id, String authorization);

    void handleReport(Long id, ReportHandleDTO reportHandleDTO, String authorization);
}
