package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.ReportCreateDTO;
import com.example.backend.dto.ReportHandleDTO;
import com.example.backend.service.ReportService;
import com.example.backend.vo.PageResultVO;
import com.example.backend.vo.ReportCreateVO;
import com.example.backend.vo.ReportDetailVO;
import com.example.backend.vo.ReportListItemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/api/reports")
    public Result<ReportCreateVO> createReport(@RequestBody @Valid ReportCreateDTO reportCreateDTO,
                                               @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(reportService.createReport(reportCreateDTO, authorization));
    }

    @GetMapping("/api/admin/reports")
    public Result<PageResultVO<ReportListItemVO>> listReports(@RequestParam(required = false) Integer pageNum,
                                                              @RequestParam(required = false) Integer pageSize,
                                                              @RequestParam(required = false) String handleStatus,
                                                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(reportService.listReports(pageNum, pageSize, handleStatus, authorization));
    }

    @GetMapping("/api/admin/reports/{id}")
    public Result<ReportDetailVO> getReportDetail(@PathVariable Long id,
                                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.success(reportService.getReportDetail(id, authorization));
    }

    @PutMapping("/api/admin/reports/{id}/handle")
    public Result<Void> handleReport(@PathVariable Long id,
                                     @RequestBody @Valid ReportHandleDTO reportHandleDTO,
                                     @RequestHeader(value = "Authorization", required = false) String authorization) {
        reportService.handleReport(id, reportHandleDTO, authorization);
        return Result.success();
    }
}
