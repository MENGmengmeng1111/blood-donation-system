package com.sdut.blood.domain.vo;

import com.sdut.blood.domain.dto.AiChatHistoryMessage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话会话展示对象
 */
@Data
public class AiChatSessionVO {

    private Long id;

    private String title;

    private List<AiChatHistoryMessage> messages;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
