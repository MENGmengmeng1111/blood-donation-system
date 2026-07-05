package com.sdut.blood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.domain.dto.AiChatHistoryMessage;
import com.sdut.blood.domain.entity.AiChatSession;
import com.sdut.blood.domain.vo.AiChatSessionVO;

import java.util.List;

/**
 * AI对话会话服务
 */
public interface AiChatSessionService extends IService<AiChatSession> {

    List<AiChatSessionVO> listMySessions();

    AiChatSessionVO getMySession(Long sessionId);

    AiChatSession prepareCurrentUserSession(Long sessionId, String question);

    List<AiChatHistoryMessage> getSessionMessages(Long sessionId);

    List<AiChatHistoryMessage> resolvePromptHistory(Long sessionId, List<AiChatHistoryMessage> fallbackHistory);

    void appendTurn(Long sessionId, String question, String answer);

    void deleteMySession(Long sessionId);
}
