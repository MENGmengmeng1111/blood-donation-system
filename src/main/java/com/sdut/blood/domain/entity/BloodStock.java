package com.sdut.blood.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 血液库存表
 */
@Data
@TableName("blood_stock")
public class BloodStock {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联采血记录ID
     */
    private Long collectionId;

    /**
     * 血型
     */
    private String bloodType;

    /**
     * 血量（ml）
     */
    private Integer bloodAmount;

    /**
     * 有效期
     */
    private LocalDate expireDate;

    /**
     * 库存状态：正常/临期/已过期/已出库
     */
    private String status;

    /**
     * 用血单位
     */
    private String outUnit;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}