SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `blood_donation`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE `blood_donation`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `appointment`;
DROP TABLE IF EXISTS `blood_stock`;
DROP TABLE IF EXISTS `blood_test`;
DROP TABLE IF EXISTS `blood_collection`;
DROP TABLE IF EXISTS `donor`;
DROP TABLE IF EXISTS `blood_activity`;
DROP TABLE IF EXISTS `operation_log`;
DROP TABLE IF EXISTS `stock_threshold`;
DROP TABLE IF EXISTS `ai_chat_session`;
DROP TABLE IF EXISTS `sys_user`;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码（BCrypt加密）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色：ROLE_DONOR/ROLE_ADMIN/ROLE_SUPER_ADMIN',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username` (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户账号表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_chat_session
-- ----------------------------
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

-- ----------------------------
-- Table structure for donor
-- ----------------------------
CREATE TABLE `donor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '献血者姓名',
  `id_card` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号（加密存储）',
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '血型：A型/B型/O型/AB型',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系电话',
  `medical_history` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '病史（加密）',
  `donor_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '正常' COMMENT '献血状态：正常/暂缓/永久淘汰',
  `last_donate_date` date NULL DEFAULT NULL COMMENT '上次献血日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `attention_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否重点关注：0-否，1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `donor_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '献血者档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for blood_activity
-- ----------------------------
CREATE TABLE `blood_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动主键ID',
  `activity_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动名称',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动地点',
  `activity_date` date NOT NULL COMMENT '活动开展日期',
  `morning_quota` int NOT NULL DEFAULT 0 COMMENT '上午总名额',
  `afternoon_quota` int NOT NULL DEFAULT 0 COMMENT '下午总名额',
  `morning_remaining` int NOT NULL DEFAULT 0 COMMENT '上午剩余名额',
  `afternoon_remaining` int NOT NULL DEFAULT 0 COMMENT '下午剩余名额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '未开始' COMMENT '状态：未开始/进行中/已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '献血招募活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
CREATE TABLE `appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约主键ID',
  `user_id` bigint NOT NULL COMMENT '献血者用户ID',
  `activity_id` bigint NOT NULL COMMENT '关联活动ID',
  `time_slot` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约时段：上午/下午',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '待参加' COMMENT '状态：待参加/已取消/已完成/已失效',
  `appointment_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约提交时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_appointment_user_id` (`user_id`) USING BTREE,
  INDEX `idx_appointment_activity_id` (`activity_id`) USING BTREE,
  CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`activity_id`) REFERENCES `blood_activity` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '献血预约记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for blood_collection
-- ----------------------------
CREATE TABLE `blood_collection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '采血记录ID',
  `donor_id` bigint NOT NULL COMMENT '献血者档案ID',
  `donor_id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '献血者身份证密文',
  `donate_amount` int NOT NULL COMMENT '献血量(ml/治疗量)',
  `donate_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '献血类型：全血/成分血',
  `initial_screen_result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '初筛结果：合格/不合格',
  `collection_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '采血时间',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作管理员ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_blood_collection_donor_id` (`donor_id`) USING BTREE,
  INDEX `idx_blood_collection_operator_id` (`operator_id`) USING BTREE,
  CONSTRAINT `blood_collection_ibfk_1` FOREIGN KEY (`donor_id`) REFERENCES `donor` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `blood_collection_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '采血记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for blood_test
-- ----------------------------
CREATE TABLE `blood_test` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '检验记录ID',
  `collection_id` bigint NOT NULL COMMENT '关联采血记录ID',
  `donor_id` bigint NOT NULL COMMENT '献血者档案ID',
  `recheck_result` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '复检详细结果',
  `blood_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '待检验' COMMENT '血液状态：待检验/合格/不合格/已入库',
  `unqualified_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '不合格原因',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `judge_time` datetime NULL DEFAULT NULL COMMENT '判定时间',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '判定管理员ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blood_test_collection_id` (`collection_id`) USING BTREE,
  INDEX `idx_blood_test_donor_id` (`donor_id`) USING BTREE,
  INDEX `idx_blood_test_operator_id` (`operator_id`) USING BTREE,
  CONSTRAINT `blood_test_ibfk_1` FOREIGN KEY (`collection_id`) REFERENCES `blood_collection` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `blood_test_ibfk_2` FOREIGN KEY (`donor_id`) REFERENCES `donor` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `blood_test_ibfk_3` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '血液检验记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for blood_stock
-- ----------------------------
CREATE TABLE `blood_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存明细ID',
  `collection_id` bigint NOT NULL COMMENT '关联采血记录ID',
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '血型',
  `blood_amount` int NOT NULL COMMENT '血量ml',
  `expire_date` date NOT NULL COMMENT '血液有效期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '正常' COMMENT '库存状态：正常/临期/已过期/已出库',
  `out_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用血单位',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blood_stock_collection_id` (`collection_id`) USING BTREE,
  CONSTRAINT `blood_stock_ibfk_1` FOREIGN KEY (`collection_id`) REFERENCES `blood_collection` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '血液库存明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for stock_threshold
-- ----------------------------
CREATE TABLE `stock_threshold` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '阈值ID',
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '血型',
  `threshold_value` int NOT NULL DEFAULT 5000 COMMENT '安全库存阈值（ml）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blood_type` (`blood_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '库存阈值配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人姓名',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型：新增/修改/删除/导出等',
  `operation_content` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作内容详情',
  `operation_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_log_operator_id` (`operator_id`) USING BTREE,
  INDEX `idx_operation_log_operation_time` (`operation_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Initial accounts
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role`, `status`, `create_time`, `update_time`, `deleted`) VALUES
(1, 'admin', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '管理员', 'ROLE_ADMIN', 0, NOW(), NOW(), 0),
(2, 'superadmin', '$2a$10$g75E5/0RPtBhRUhfl5.j3.KqPUs1ohEWHdH7UNFMgYdXjc1amI1Qy', '超级管理员', 'ROLE_SUPER_ADMIN', 0, NOW(), NOW(), 0),
(3, 'donor001', '$2a$10$96VA1mfKlA9HykWBqotDwePic2zY/W07BFFfOGxj5FgYzCC06zR9C', '张三', 'ROLE_DONOR', 0, NOW(), NOW(), 0);

-- ----------------------------
-- Initial stock thresholds
-- ----------------------------
INSERT INTO `stock_threshold` (`id`, `blood_type`, `threshold_value`, `update_time`, `update_by`) VALUES
(1, 'A型', 5000, NOW(), NULL),
(2, 'B型', 5000, NOW(), NULL),
(3, 'O型', 5000, NOW(), NULL),
(4, 'AB型', 5000, NOW(), NULL);

SET FOREIGN_KEY_CHECKS = 1;
