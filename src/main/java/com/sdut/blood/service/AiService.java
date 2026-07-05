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

    /**
     * 用户侧智能咨询问答，指定会话并携带最近多轮上下文
     */
    Result<AiChatResponse> chatForDonor(Long sessionId, String question, List<AiChatHistoryMessage> history);

    /**
     * 用户侧智能咨询问答流式输出，携带最近多轮上下文
     */
    void streamChatForDonor(String question, List<AiChatHistoryMessage> history, AiStreamCallback callback);

    /**
     * 用户侧智能咨询问答流式输出，指定会话并携带上下文
     */
    void streamChatForDonor(Long sessionId, String question, List<AiChatHistoryMessage> history, AiStreamCallback callback);
}
