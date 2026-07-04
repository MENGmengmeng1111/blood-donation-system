package com.sdut.blood.service;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.vo.StatisticsVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取多维度统计数据（UC39）
     */
    StatisticsVO getStatistics();

    /**
     * 获取不合格血液原因分布
     */
    Result<?> getUnqualifiedReasonDistribution();

    /**
     * 获取献血者年龄分布
     */
    Result<?> getAgeDistribution();

    /**
     * 获取献血者性别分布
     */
    Result<?> getGenderDistribution();

    /**
     * 获取月度献血趋势
     */
    Result<?> getMonthlyDonateTrend();
}