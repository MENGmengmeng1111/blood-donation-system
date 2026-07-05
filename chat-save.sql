CREATE TABLE `ai_chat_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'AI对话会话ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '新的对话' COMMENT '会话标题',
  `messages_json` json NOT NULL COMMENT '完整对话消息JSON数组',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ai_chat_session_user_id` (`user_id`) USING BTREE,
  INDEX `idx_ai_chat_session_update_time` (`update_time`) USING BTREE,
  CONSTRAINT `ai_chat_session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI对话会话表' ROW_FORMAT = DYNAMIC;
