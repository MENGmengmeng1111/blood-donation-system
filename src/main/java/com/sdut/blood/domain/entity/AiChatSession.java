package com.sdut.blood.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI对话会话表
 */
@Data
@TableName("ai_chat_session")
public class AiChatSession {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话所属用户ID
     */
    private Long userId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 完整对话消息JSON
     */
    private String messagesJson;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
