package com.sdut.blood.domain.dto;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * AI多轮对话历史消息
 */
@Data
public class AiChatHistoryMessage {

    /**
     * user 或 assistant
     */
    private String role;

    /**
     * 历史消息内容
     */
    @Size(max = 10000, message = "历史消息内容过长")
    private String content;
}
