package com.sdut.blood.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("blood_test_indicator")
public class BloodTestIndicator {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long testId;

    private Double alt;

    private String hbvSurfaceAntigen;

    private String hcvAntibody;

    private String hivAntibody;

    private String syphilisAntibody;

    private Double whiteBloodCell;

    private Double hemoglobin;

    private Double platelet;

    private String otherAbnormality;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private Long collectionId;

    @TableField(exist = false)
    private Long donorId;

    @TableField(exist = false)
    private String donorName;

    @TableField(exist = false)
    private String donorGender;

    @TableField(exist = false)
    private String donorBloodType;

    @TableField(exist = false)
    private Integer donorAge;

    @TableField(exist = false)
    private Integer donateAmount;

    @TableField(exist = false)
    private String donateType;

    @TableField(exist = false)
    private LocalDateTime collectionTime;

    @TableField(exist = false)
    private String batchNo;

    @TableField(exist = false)
    private String bloodStatus;

    @TableField(exist = false)
    private String recheckResult;

    @TableField(exist = false)
    private String unqualifiedReason;

    @TableField(exist = false)
    private Long indicatorId;
}