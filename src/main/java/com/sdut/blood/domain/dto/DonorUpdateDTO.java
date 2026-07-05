package com.sdut.blood.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改献血者档案入参
 */
@Data
public class DonorUpdateDTO {

    @NotNull(message = "档案ID不能为空")
    private Long id;

    @NotBlank(message = "姓名不可为空")
    private String name;

    @NotBlank(message = "联系电话不可为空")
    private String phone;

    private String bloodType;

    private String medicalHistory;

    private String donorStatus;

    private String idCard;

    private String gender;

    private String address;
}
