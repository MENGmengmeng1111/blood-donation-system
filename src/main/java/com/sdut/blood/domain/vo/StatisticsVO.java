package com.sdut.blood.domain.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 统计数据视图
 */
@Data
public class StatisticsVO {

    private Integer totalDonateAmount;
    
    private Integer totalUseAmount;
    
    private Integer totalDonors;
    
    private Integer totalActivities;
    
    private List<Map<String, Object>> unqualifiedReasonDistribution;
    
    private List<Map<String, Object>> ageDistribution;
    
    private List<Map<String, Object>> genderDistribution;
    
    private List<Map<String, Object>> bloodTypeDistribution;
    
    private List<Map<String, Object>> monthlyDonateTrend;
}