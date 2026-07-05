package com.sdut.blood.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 血液合格状态判定入参
 */
@Data
public class BloodTestJudgeDTO {

    @NotNull(message = "请选择待判定的检验记录")
    private Long testId;

    @NotBlank(message = "请选择血液判定状态")
    private String bloodStatus;

    /**
     * 不合格原因（不合格时必填）
     */
    private String unqualifiedReason;

    /**
     * 复检结果
     */
    private String recheckResult;

    /**
     * 判定备注
     */
    private String remark;
}