package com.sdut.blood.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * AI咨询响应
 */
@Data
public class AiChatResponse {

    private String answer;

    private List<String> references;

    private Boolean personalized;
}
