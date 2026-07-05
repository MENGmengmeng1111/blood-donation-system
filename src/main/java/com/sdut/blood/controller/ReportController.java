package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.ReportQueryDTO;
import com.sdut.blood.domain.vo.ReportVO;
import com.sdut.blood.service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 报表统计控制器
 */
@RestController
@RequestMapping("/api/report")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class ReportController {

    @Resource
    private ReportService reportService;

    /**
     * 生成统计结果（表格+图表数据）
     */
    @PostMapping("/generate")
    public Result<ReportVO> generateReport(@Valid @RequestBody ReportQueryDTO dto) {
        ReportVO reportVO = reportService.generateReport(dto);
        return Result.success(reportVO);
    }

    /**
     * 导出Excel报表（UC40）
     * 浏览器直接访问该接口即可触发文件下载
     */
    @GetMapping("/export")
    public void exportExcel(ReportQueryDTO dto, HttpServletResponse response) {
        reportService.exportExcel(dto, response);
    }
}
