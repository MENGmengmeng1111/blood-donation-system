package com.sdut.blood.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CollectionUpdateDTO {

    @NotNull(message = "请选择待修改的采血记录")
    private Long id;

    @NotNull(message = "请填写献血量")
    private Integer donateAmount;

    @NotBlank(message = "请选择献血类型")
    private String donateType;

    @NotBlank(message = "请选择初筛结果")
    private String initialScreenResult;
}