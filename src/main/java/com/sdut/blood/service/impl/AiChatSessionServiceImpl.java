package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.dto.AiChatHistoryMessage;
import com.sdut.blood.domain.entity.AiChatSession;
import com.sdut.blood.domain.vo.AiChatSessionVO;
import com.sdut.blood.mapper.AiChatSessionMapper;
import com.sdut.blood.service.AiChatSessionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AI对话会话服务实现
 */
@Service
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession>
        implements AiChatSessionService {

    private static final String EMPTY_MESSAGES_JSON = "[]";

    private static final int MAX_TITLE_LENGTH = 30;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<AiChatSessionVO> listMySessions() {
        Long userId = requireCurrentUserId();
        List<AiChatSession> sessions = list(new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getUserId, userId)
                .orderByDesc(AiChatSession::getUpdateTime));
        List<AiChatSessionVO> result = new ArrayList<>();
        for (AiChatSession session : sessions) {
            AiChatSessionVO vo = convertToVO(session);
            vo.setMessages(List.of());
            result.add(vo);
        }
        return result;
    }

    @Override
    public AiChatSessionVO getMySession(Long sessionId) {
        AiChatSession session = getOwnedSession(sessionId);
        return convertToVO(session);
    }

    @Override
    public AiChatSession prepareCurrentUserSession(Long sessionId, String question) {
        Long userId = requireCurrentUserId();
        if (sessionId != null) {
            return getOwnedSession(sessionId);
        }

        AiChatSession session = new AiChatSession();
        session.setUserId(userId);
        session.setTitle(buildTitle(question));
        session.setMessagesJson(EMPTY_MESSAGES_JSON);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        session.setDeleted(0);
        save(session);
        return session;
    }

    @Override
    public List<AiChatHistoryMessage> getSessionMessages(Long sessionId) {
        if (sessionId == null) {
            return List.of();
        }
        return parseMessages(getOwnedSession(sessionId).getMessagesJson());
    }

    @Override
    public List<AiChatHistoryMessage> resolvePromptHistory(Long sessionId, List<AiChatHistoryMessage> fallbackHistory) {
        List<AiChatHistoryMessage> storedMessages = getSessionMessages(sessionId);
        if (!storedMessages.isEmpty()) {
            return storedMessages;
        }
        return fallbackHistory == null ? List.of() : fallbackHistory;
    }

    @Override
    public void appendTurn(Long sessionId, String question, String answer) {
        if (sessionId == null || !StringUtils.hasText(question) || !StringUtils.hasText(answer)) {
            return;
        }

        AiChatSession session = getOwnedSession(sessionId);
        List<AiChatHistoryMessage> messages = new ArrayList<>(parseMessages(session.getMessagesJson()));
        messages.add(buildMessage("user", question));
        messages.add(buildMessage("assistant", answer));

        session.setMessagesJson(writeMessages(messages));
        session.setUpdateTime(LocalDateTime.now());
        updateById(session);
    }

    @Override
    public void deleteMySession(Long sessionId) {
        AiChatSession session = getOwnedSession(sessionId);
        removeById(session.getId());
    }

    private AiChatSession getOwnedSession(Long sessionId) {
        Long userId = requireCurrentUserId();
        AiChatSession session = getOne(new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getId, sessionId)
                .eq(AiChatSession::getUserId, userId)
                .last("LIMIT 1"));
        if (session == null) {
            throw new IllegalArgumentException("对话不存在或无权访问");
        }
        return session;
    }

    private AiChatSessionVO convertToVO(AiChatSession session) {
        AiChatSessionVO vo = new AiChatSessionVO();
        vo.setId(session.getId());
        vo.setTitle(session.getTitle());
        vo.setMessages(parseMessages(session.getMessagesJson()));
        vo.setCreateTime(session.getCreateTime());
        vo.setUpdateTime(session.getUpdateTime());
        return vo;
    }

    private List<AiChatHistoryMessage> parseMessages(String messagesJson) {
        if (!StringUtils.hasText(messagesJson)) {
            return List.of();
        }
        try {
            List<AiChatHistoryMessage> messages = objectMapper.readValue(
                    messagesJson,
                    new TypeReference<List<AiChatHistoryMessage>>() {
                    });
            return messages == null ? List.of() : messages;
        } catch (Exception e) {
            return List.of();
        }
    }

    private String writeMessages(List<AiChatHistoryMessage> messages) {
        try {
            return objectMapper.writeValueAsString(messages);
        } catch (Exception e) {
            return EMPTY_MESSAGES_JSON;
        }
    }

    private AiChatHistoryMessage buildMessage(String role, String content) {
        AiChatHistoryMessage message = new AiChatHistoryMessage();
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    private String buildTitle(String question) {
        if (!StringUtils.hasText(question)) {
            return "新的对话";
        }
        String title = question.trim().replaceAll("\\s+", " ");
        if (title.length() <= MAX_TITLE_LENGTH) {
            return title;
        }
        return title.substring(0, MAX_TITLE_LENGTH) + "...";
    }

    private Long requireCurrentUserId() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("请先登录");
        }
        return userId;
    }
}
