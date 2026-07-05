package com.sdut.blood.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 血液检验表
 */
@Data
@TableName("blood_test")
public class BloodTest {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联采血记录ID
     */
    private Long collectionId;

    /**
     * 关联献血者ID
     */
    private Long donorId;

    /**
     * 复检结果
     */
    private String recheckResult;

    /**
     * 血液最终状态：合格/不合格/待销毁
     */
    private String bloodStatus;

    /**
     * 不合格原因
     */
    private String unqualifiedReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 判定时间
     */
    private LocalDateTime judgeTime;

    /**
     * 判定操作人ID
     */
    private Long operatorId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}