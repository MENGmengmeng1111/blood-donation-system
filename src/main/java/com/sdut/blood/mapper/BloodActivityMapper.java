package com.sdut.blood.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdut.blood.domain.entity.BloodActivity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 献血活动 Mapper
 */
public interface BloodActivityMapper extends BaseMapper<BloodActivity> {

    /**
     * 查询所有可预约的活动（未开始状态）
     */
    List<BloodActivity> selectAvailableActivityList();

    /**
     * 原子扣减上午剩余名额
     */
    int decreaseMorningQuota(@Param("activityId") Long activityId);

    /**
     * 原子扣减下午剩余名额
     */
    int decreaseAfternoonQuota(@Param("activityId") Long activityId);

    /**
     * 原子恢复上午剩余名额
     */
    int increaseMorningQuota(@Param("activityId") Long activityId);

    /**
     * 原子恢复下午剩余名额
     */
    int increaseAfternoonQuota(@Param("activityId") Long activityId);
}
