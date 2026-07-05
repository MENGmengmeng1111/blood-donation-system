package com.sdut.blood.domain.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * AI咨询请求
 */
@Data
public class AiChatRequest {

    /**
     * 当前对话会话ID，为空时由后端自动创建新会话
     */
    private Long sessionId;

    @NotBlank(message = "请输入咨询问题")
    private String question;

    /**
     * 最近多轮对话历史，由前端按时间顺序传入
     */
    @Valid
    @Size(max = 8, message = "历史消息最多保留8条")
    private List<AiChatHistoryMessage> history;
}
