package com.sdut.blood.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 献血活动表
 */
@Data
@TableName("blood_activity")
public class BloodActivity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 活动日期
     */
    private LocalDate activityDate;

    /**
     * 上午时段总名额
     */
    private Integer morningQuota;

    /**
     * 下午时段总名额
     */
    private Integer afternoonQuota;

    /**
     * 上午剩余名额
     */
    private Integer morningRemaining;

    /**
     * 下午剩余名额
     */
    private Integer afternoonRemaining;

    /**
     * 活动状态：未开始/进行中/已结束
     */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}