package com.sdut.blood.controller;

import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.BloodStockService;
import com.sdut.blood.service.BloodTestService;
import com.sdut.blood.service.DonorService;
import com.sdut.blood.service.ExcelExportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Excel导出控制器（UC40）
 */
@RestController
@RequestMapping("/api/export")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class ExcelExportController {

    @Resource
    private ExcelExportService excelExportService;

    @Resource
    private DonorService donorService;

    @Resource
    private BloodCollectionService bloodCollectionService;

    @Resource
    private BloodTestService bloodTestService;

    @Resource
    private BloodStockService bloodStockService;

    /**
     * 导出献血者档案
     */
    @GetMapping("/donors")
    public void exportDonors(HttpServletResponse response) throws IOException {
        setExcelResponseHeader(response, "献血者档案");
        try (OutputStream outputStream = response.getOutputStream()) {
            var data = excelExportService.exportDonors(donorService.list());
            outputStream.write(data.toByteArray());
        }
    }

    /**
     * 导出采血记录
     */
    @GetMapping("/collections")
    public void exportCollections(HttpServletResponse response) throws IOException {
        setExcelResponseHeader(response, "采血记录");
        try (OutputStream outputStream = response.getOutputStream()) {
            var data = excelExportService.exportCollections(bloodCollectionService.list());
            outputStream.write(data.toByteArray());
        }
    }

    /**
     * 导出检验记录
     */
    @GetMapping("/tests")
    public void exportTests(HttpServletResponse response) throws IOException {
        setExcelResponseHeader(response, "检验记录");
        try (OutputStream outputStream = response.getOutputStream()) {
            var data = excelExportService.exportTests(bloodTestService.list());
            outputStream.write(data.toByteArray());
        }
    }

    /**
     * 导出库存记录
     */
    @GetMapping("/stocks")
    public void exportStocks(HttpServletResponse response) throws IOException {
        setExcelResponseHeader(response, "库存记录");
        try (OutputStream outputStream = response.getOutputStream()) {
            var data = excelExportService.exportStocks(bloodStockService.list());
            outputStream.write(data.toByteArray());
        }
    }

    /**
     * 导出统计报表
     */
    @GetMapping("/statistics")
    public void exportStatistics(HttpServletResponse response) throws IOException {
        setExcelResponseHeader(response, "统计报表");
        try (OutputStream outputStream = response.getOutputStream()) {
            var data = excelExportService.exportStatistics();
            outputStream.write(data.toByteArray());
        }
    }

    private void setExcelResponseHeader(HttpServletResponse response, String fileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String encodedFileName = URLEncoder.encode(fileName + "_" + timestamp + ".xlsx", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setCharacterEncoding("UTF-8");
    }
}