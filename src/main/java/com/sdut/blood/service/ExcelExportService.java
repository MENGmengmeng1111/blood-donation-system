package com.sdut.blood.service;

import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.Donor;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Excel导出服务接口（UC40）
 */
public interface ExcelExportService {

    /**
     * 导出献血者档案
     */
    ByteArrayOutputStream exportDonors(List<Donor> donors);

    /**
     * 导出采血记录
     */
    ByteArrayOutputStream exportCollections(List<BloodCollection> collections);

    /**
     * 导出检验记录
     */
    ByteArrayOutputStream exportTests(List<BloodTest> tests);

    /**
     * 导出库存记录
     */
    ByteArrayOutputStream exportStocks(List<BloodStock> stocks);

    /**
     * 导出统计报表
     */
    ByteArrayOutputStream exportStatistics();
}