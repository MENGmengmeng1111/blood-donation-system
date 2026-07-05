package com.sdut.blood.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 新增献血者档案入参
 */
@Data
public class DonorAddDTO {

    private Long userId;

    @NotBlank(message = "请填写姓名")
    private String name;

    @NotBlank(message = "请填写身份证号")
    private String idCard;

    @NotBlank(message = "请选择血型")
    private String bloodType;

    @NotBlank(message = "请填写联系电话")
    private String phone;

    private String gender;

    private String address;

    /**
     * 病史（非必填）
     */
    private String medicalHistory;
}
