package com.sdut.blood.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 血液检验记录基础信息修改入参
 */
@Data
public class BloodTestUpdateDTO {

    @NotNull(message = "检验记录ID不能为空")
    private Long id;

    /**
     * 复检结果
     */
    private String recheckResult;

    /**
     * 不合格原因
     */
    private String unqualifiedReason;

    /**
     * 备注
     */
    private String remark;
}
