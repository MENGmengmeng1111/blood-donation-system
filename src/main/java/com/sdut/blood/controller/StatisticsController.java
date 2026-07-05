package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.vo.StatisticsVO;
import com.sdut.blood.service.StatisticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 统计控制器（UC38-UC40）
 */
@RestController
@RequestMapping("/api/statistics")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 获取多维度统计数据（UC39）
     */
    @GetMapping("/overview")
    public Result<StatisticsVO> getStatistics() {
        StatisticsVO statistics = statisticsService.getStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取不合格血液原因分布
     */
    @GetMapping("/unqualified-reason")
    public Result<?> getUnqualifiedReasonDistribution() {
        return statisticsService.getUnqualifiedReasonDistribution();
    }

    /**
     * 获取献血者年龄分布
     */
    @GetMapping("/age-distribution")
    public Result<?> getAgeDistribution() {
        return statisticsService.getAgeDistribution();
    }

    /**
     * 获取献血者性别分布
     */
    @GetMapping("/gender-distribution")
    public Result<?> getGenderDistribution() {
        return statisticsService.getGenderDistribution();
    }

    /**
     * 获取月度献血趋势
     */
    @GetMapping("/monthly-trend")
    public Result<?> getMonthlyDonateTrend() {
        return statisticsService.getMonthlyDonateTrend();
    }
}
