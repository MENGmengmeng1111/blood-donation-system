package com.sdut.blood.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * AI咨询请求
 */
@Data
public class AiChatRequest {

    @NotBlank(message = "请输入咨询问题")
    private String question;
}
