package com.sdut.blood.service.impl;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.BloodCollection;
import com.sdut.blood.domain.entity.BloodStock;
import com.sdut.blood.domain.entity.BloodTest;
import com.sdut.blood.domain.entity.Donor;
import com.sdut.blood.domain.vo.StatisticsVO;
import com.sdut.blood.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private BloodCollectionService bloodCollectionService;

    @Resource
    private BloodStockService bloodStockService;

    @Resource
    private BloodTestService bloodTestService;

    @Resource
    private DonorService donorService;

    @Resource
    private BloodActivityService bloodActivityService;

    @Override
    public StatisticsVO getStatistics() {
        StatisticsVO vo = new StatisticsVO();
        
        vo.setTotalDonateAmount(getTotalDonateAmount());
        vo.setTotalUseAmount(getTotalUseAmount());
        vo.setTotalDonors((int) donorService.count());
        vo.setTotalActivities((int) bloodActivityService.count());
        vo.setUnqualifiedReasonDistribution(getUnqualifiedReasonDistributionData());
        vo.setAgeDistribution(getAgeDistributionData());
        vo.setGenderDistribution(getGenderDistributionData());
        vo.setBloodTypeDistribution(getBloodTypeDistribution());
        vo.setMonthlyDonateTrend(getMonthlyDonateTrendData());
        
        return vo;
    }

    private Integer getTotalDonateAmount() {
        List<BloodCollection> list = bloodCollectionService.list();
        return list.stream().mapToInt(BloodCollection::getDonateAmount).sum();
    }

    private Integer getTotalUseAmount() {
        List<BloodStock> list = bloodStockService.list(new LambdaQueryWrapper<BloodStock>()
                .eq(BloodStock::getStatus, "已出库"));
        return list.stream().mapToInt(BloodStock::getBloodAmount).sum();
    }

    @Override
    public Result<?> getUnqualifiedReasonDistribution() {
        return Result.success(getUnqualifiedReasonDistributionData());
    }

    private List<Map<String, Object>> getUnqualifiedReasonDistributionData() {
        List<BloodTest> list = bloodTestService.list(new LambdaQueryWrapper<BloodTest>()
                .eq(BloodTest::getBloodStatus, "不合格"));
        
        Map<String, Long> reasonCount = list.stream()
                .filter(t -> t.getUnqualifiedReason() != null && !t.getUnqualifiedReason().trim().isEmpty())
                .collect(Collectors.groupingBy(t -> {
                    String reason = t.getUnqualifiedReason();
                    if (reason.contains("乙肝") || reason.contains("HBV")) return "乙肝病毒";
                    if (reason.contains("丙肝") || reason.contains("HCV")) return "丙肝病毒";
                    if (reason.contains("艾滋病") || reason.contains("HIV")) return "艾滋病病毒";
                    if (reason.contains("梅毒")) return "梅毒";
                    if (reason.contains("转氨酶") || reason.contains("ALT")) return "转氨酶偏高";
                    if (reason.contains("血红蛋白")) return "血红蛋白异常";
                    if (reason.contains("血型")) return "血型不符";
                    if (reason.contains("过期") || reason.contains("临期")) return "血液过期";
                    return "其他";
                }, Collectors.counting()));
        
        List<Map<String, Object>> result = new ArrayList<>();
        reasonCount.forEach((reason, count) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("reason", reason);
            map.put("count", count);
            result.add(map);
        });
        
        return result;
    }

    @Override
    public Result<?> getAgeDistribution() {
        return Result.success(getAgeDistributionData());
    }

    private List<Map<String, Object>> getAgeDistributionData() {
        List<Donor> list = donorService.list();
        
        Map<String, Long> ageCount = list.stream()
                .filter(d -> d.getAge() != null)
                .collect(Collectors.groupingBy(d -> {
                    int age = d.getAge();
                    if (age < 18) return "未满18岁";
                    if (age <= 25) return "18-25岁";
                    if (age <= 35) return "26-35岁";
                    if (age <= 45) return "36-45岁";
                    if (age <= 55) return "46-55岁";
                    return "55岁以上";
                }, Collectors.counting()));
        
        List<Map<String, Object>> result = new ArrayList<>();
        ageCount.forEach((age, count) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ageGroup", age);
            map.put("count", count);
            result.add(map);
        });
        
        return result;
    }

    @Override
    public Result<?> getGenderDistribution() {
        return Result.success(getGenderDistributionData());
    }

    private List<Map<String, Object>> getGenderDistributionData() {
        List<Donor> list = donorService.list();
        
        Map<String, Long> genderCount = list.stream()
                .filter(d -> d.getGender() != null && !d.getGender().trim().isEmpty())
                .collect(Collectors.groupingBy(Donor::getGender, Collectors.counting()));
        
        List<Map<String, Object>> result = new ArrayList<>();
        genderCount.forEach((gender, count) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("gender", gender);
            map.put("count", count);
            result.add(map);
        });
        
        return result;
    }

    private List<Map<String, Object>> getBloodTypeDistribution() {
        List<Donor> list = donorService.list();
        
        Map<String, Long> bloodTypeCount = list.stream()
                .filter(d -> d.getBloodType() != null && !d.getBloodType().trim().isEmpty())
                .collect(Collectors.groupingBy(Donor::getBloodType, Collectors.counting()));
        
        List<Map<String, Object>> result = new ArrayList<>();
        bloodTypeCount.forEach((type, count) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("bloodType", type);
            map.put("count", count);
            result.add(map);
        });
        
        return result;
    }

    @Override
    public Result<?> getMonthlyDonateTrend() {
        return Result.success(getMonthlyDonateTrendData());
    }

    private List<Map<String, Object>> getMonthlyDonateTrendData() {
        List<BloodCollection> list = bloodCollectionService.list();
        
        Map<String, Integer> monthAmount = list.stream()
                .filter(c -> c.getCollectionTime() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getCollectionTime().toString().substring(0, 7),
                        Collectors.summingInt(BloodCollection::getDonateAmount)
                ));
        
        List<String> sortedMonths = monthAmount.keySet().stream().sorted().collect(Collectors.toList());
        
        List<Map<String, Object>> result = new ArrayList<>();
        sortedMonths.forEach(month -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", month);
            map.put("amount", monthAmount.get(month));
            result.add(map);
        });
        
        return result;
    }
}