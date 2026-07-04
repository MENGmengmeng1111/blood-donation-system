package com.sdut.blood.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.StatisticsVO;
import com.sdut.blood.service.BloodCollectionService;
import com.sdut.blood.service.BloodStockService;
import com.sdut.blood.service.BloodTestService;
import com.sdut.blood.service.DonorService;
import com.sdut.blood.service.ExcelExportService;
import com.sdut.blood.service.StatisticsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Resource
    private DonorService donorService;

    @Resource
    private BloodCollectionService bloodCollectionService;

    @Resource
    private BloodTestService bloodTestService;

    @Resource
    private BloodStockService bloodStockService;

    @Resource
    private StatisticsService statisticsService;

    @Override
    public ByteArrayOutputStream exportDonors(List<Donor> donors) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, DonorExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("献血者档案")
                .doWrite(donors.stream().map(DonorExportDTO::new).toList());
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportCollections(List<BloodCollection> collections) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, CollectionExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("采血记录")
                .doWrite(collections.stream().map(CollectionExportDTO::new).toList());
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportTests(List<BloodTest> tests) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, TestExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("检验记录")
                .doWrite(tests.stream().map(TestExportDTO::new).toList());
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportStocks(List<BloodStock> stocks) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, StockExportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("库存记录")
                .doWrite(stocks.stream().map(StockExportDTO::new).toList());
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream exportStatistics() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StatisticsVO statistics = statisticsService.getStatistics();

        com.alibaba.excel.ExcelWriter excelWriter = EasyExcel.write(outputStream)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .build();

        com.alibaba.excel.write.metadata.WriteSheet overviewSheet = new com.alibaba.excel.write.metadata.WriteSheet();
        overviewSheet.setSheetNo(0);
        overviewSheet.setSheetName("统计概览");
        excelWriter.write(getOverviewData(statistics), overviewSheet);

        com.alibaba.excel.write.metadata.WriteSheet reasonSheet = new com.alibaba.excel.write.metadata.WriteSheet();
        reasonSheet.setSheetNo(1);
        reasonSheet.setSheetName("不合格原因分布");
        excelWriter.write(getUnqualifiedReasonData(statistics.getUnqualifiedReasonDistribution()), reasonSheet);

        com.alibaba.excel.write.metadata.WriteSheet ageSheet = new com.alibaba.excel.write.metadata.WriteSheet();
        ageSheet.setSheetNo(2);
        ageSheet.setSheetName("年龄分布");
        excelWriter.write(getAgeDistributionData(statistics.getAgeDistribution()), ageSheet);

        com.alibaba.excel.write.metadata.WriteSheet genderSheet = new com.alibaba.excel.write.metadata.WriteSheet();
        genderSheet.setSheetNo(3);
        genderSheet.setSheetName("性别分布");
        excelWriter.write(getGenderDistributionData(statistics.getGenderDistribution()), genderSheet);

        com.alibaba.excel.write.metadata.WriteSheet bloodTypeSheet = new com.alibaba.excel.write.metadata.WriteSheet();
        bloodTypeSheet.setSheetNo(4);
        bloodTypeSheet.setSheetName("血型分布");
        excelWriter.write(getBloodTypeDistributionData(statistics.getBloodTypeDistribution()), bloodTypeSheet);

        com.alibaba.excel.write.metadata.WriteSheet trendSheet = new com.alibaba.excel.write.metadata.WriteSheet();
        trendSheet.setSheetNo(5);
        trendSheet.setSheetName("月度献血趋势");
        excelWriter.write(getMonthlyTrendData(statistics.getMonthlyDonateTrend()), trendSheet);

        excelWriter.finish();
        return outputStream;
    }

    private List<List<Object>> getOverviewData(StatisticsVO statistics) {
        List<List<Object>> data = new ArrayList<>();
        data.add(List.of("统计项", "数值"));
        data.add(List.of("总献血量(ml)", statistics.getTotalDonateAmount()));
        data.add(List.of("总用血量(ml)", statistics.getTotalUseAmount()));
        data.add(List.of("献血者总数", statistics.getTotalDonors()));
        data.add(List.of("活动总数", statistics.getTotalActivities()));
        return data;
    }

    private List<List<Object>> getUnqualifiedReasonData(List<Map<String, Object>> data) {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(List.of("原因", "数量"));
        for (Map<String, Object> item : data) {
            rows.add(List.of(item.get("reason"), item.get("count")));
        }
        return rows;
    }

    private List<List<Object>> getAgeDistributionData(List<Map<String, Object>> data) {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(List.of("年龄段", "人数"));
        for (Map<String, Object> item : data) {
            rows.add(List.of(item.get("ageGroup"), item.get("count")));
        }
        return rows;
    }

    private List<List<Object>> getGenderDistributionData(List<Map<String, Object>> data) {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(List.of("性别", "人数"));
        for (Map<String, Object> item : data) {
            rows.add(List.of(item.get("gender"), item.get("count")));
        }
        return rows;
    }

    private List<List<Object>> getBloodTypeDistributionData(List<Map<String, Object>> data) {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(List.of("血型", "人数"));
        for (Map<String, Object> item : data) {
            rows.add(List.of(item.get("bloodType"), item.get("count")));
        }
        return rows;
    }

    private List<List<Object>> getMonthlyTrendData(List<Map<String, Object>> data) {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(List.of("月份", "献血量(ml)"));
        for (Map<String, Object> item : data) {
            rows.add(List.of(item.get("month"), item.get("amount")));
        }
        return rows;
    }

    public static class DonorExportDTO {
        @com.alibaba.excel.annotation.ExcelProperty("姓名")
        private String name;
        @com.alibaba.excel.annotation.ExcelProperty("身份证号")
        private String idCard;
        @com.alibaba.excel.annotation.ExcelProperty("血型")
        private String bloodType;
        @com.alibaba.excel.annotation.ExcelProperty("联系电话")
        private String phone;
        @com.alibaba.excel.annotation.ExcelProperty("性别")
        private String gender;
        @com.alibaba.excel.annotation.ExcelProperty("年龄")
        private Integer age;
        @com.alibaba.excel.annotation.ExcelProperty("献血状态")
        private String donorStatus;
        @com.alibaba.excel.annotation.ExcelProperty("是否重点关注")
        private String attentionFlag;

        public DonorExportDTO(Donor donor) {
            this.name = donor.getName();
            this.idCard = donor.getIdCard();
            this.bloodType = donor.getBloodType();
            this.phone = donor.getPhone();
            this.gender = donor.getGender();
            this.age = donor.getAge();
            this.donorStatus = donor.getDonorStatus();
            this.attentionFlag = donor.getAttentionFlag() != null && donor.getAttentionFlag() == 1 ? "是" : "否";
        }
    }

    public static class CollectionExportDTO {
        @com.alibaba.excel.annotation.ExcelProperty("献血量(ml)")
        private Integer donateAmount;
        @com.alibaba.excel.annotation.ExcelProperty("献血类型")
        private String donateType;
        @com.alibaba.excel.annotation.ExcelProperty("初筛结果")
        private String initialScreenResult;
        @com.alibaba.excel.annotation.ExcelProperty("采血时间")
        private String collectionTime;

        public CollectionExportDTO(BloodCollection collection) {
            this.donateAmount = collection.getDonateAmount();
            this.donateType = collection.getDonateType();
            this.initialScreenResult = collection.getInitialScreenResult();
            this.collectionTime = collection.getCollectionTime() != null ? collection.getCollectionTime().toString() : "";
        }
    }

    public static class TestExportDTO {
        @com.alibaba.excel.annotation.ExcelProperty("复检结果")
        private String recheckResult;
        @com.alibaba.excel.annotation.ExcelProperty("血液状态")
        private String bloodStatus;
        @com.alibaba.excel.annotation.ExcelProperty("不合格原因")
        private String unqualifiedReason;
        @com.alibaba.excel.annotation.ExcelProperty("判定时间")
        private String judgeTime;

        public TestExportDTO(BloodTest test) {
            this.recheckResult = test.getRecheckResult();
            this.bloodStatus = test.getBloodStatus();
            this.unqualifiedReason = test.getUnqualifiedReason();
            this.judgeTime = test.getJudgeTime() != null ? test.getJudgeTime().toString() : "";
        }
    }

    public static class StockExportDTO {
        @com.alibaba.excel.annotation.ExcelProperty("血型")
        private String bloodType;
        @com.alibaba.excel.annotation.ExcelProperty("血量(ml)")
        private Integer bloodAmount;
        @com.alibaba.excel.annotation.ExcelProperty("有效期")
        private String expireDate;
        @com.alibaba.excel.annotation.ExcelProperty("库存状态")
        private String status;
        @com.alibaba.excel.annotation.ExcelProperty("入库时间")
        private String createTime;

        public StockExportDTO(BloodStock stock) {
            this.bloodType = stock.getBloodType();
            this.bloodAmount = stock.getBloodAmount();
            this.expireDate = stock.getExpireDate() != null ? stock.getExpireDate().toString() : "";
            this.status = stock.getStatus();
            this.createTime = stock.getCreateTime() != null ? stock.getCreateTime().toString() : "";
        }
    }
}