package com.sdut.blood.service;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.AiChatHistoryMessage;
import com.sdut.blood.domain.vo.AiChatResponse;

import java.util.List;

/**
 * AI智能问答服务接口
 */
public interface AiService {

    /**
     * 智能咨询问答
     */
    Result<String> ask(String question);

    /**
     * 用户侧智能咨询问答
     */
    Result<AiChatResponse> chatForDonor(String question);

    /**
     * 用户侧智能咨询问答，携带最近多轮上下文
     */
    Result<AiChatResponse> chatForDonor(String question, List<AiChatHistoryMessage> history);
}
