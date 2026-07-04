package com.sdut.blood.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 献血者档案表
 */
@Data
@TableName("donor")
public class Donor {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户账号ID
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号（加密存储）
     */
    private String idCard;

    /**
     * 血型：A型/B型/O型/AB型
     */
    private String bloodType;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 地址
     */
    private String address;

    /**
     * 病史（加密存储）
     */
    private String medicalHistory;

    /**
     * 献血者状态：正常/暂缓/永久淘汰
     */
    private String donorStatus;

    /**
     * 最近一次献血日期
     */
    private LocalDate lastDonateDate;

    /**
     * 是否重点关注：0-否，1-是
     */
    private Integer attentionFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}