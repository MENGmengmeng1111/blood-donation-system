/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : localhost:3306
 Source Schema         : blood_donation

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 15/07/2026 11:10:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_session
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_session`;
CREATE TABLE `ai_chat_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'AI对话会话ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '新的对话' COMMENT '会话标题',
  `messages_json` json NOT NULL COMMENT '完整对话消息JSON数组',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ai_chat_session_user_id`(`user_id`) USING BTREE,
  INDEX `idx_ai_chat_session_update_time`(`update_time`) USING BTREE,
  CONSTRAINT `ai_chat_session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2074798728085884930 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI对话会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_chat_session
-- ----------------------------
INSERT INTO `ai_chat_session` VALUES (2074054827557269506, 3, '你好', '[]', '2026-07-06 16:56:20', '2026-07-06 17:06:35', 1);
INSERT INTO `ai_chat_session` VALUES (2074055341221179393, 3, '我该如何预约献血活动？', '[]', '2026-07-06 16:58:22', '2026-07-06 17:06:39', 1);
INSERT INTO `ai_chat_session` VALUES (2074057206918889474, 3, '我的检验结果代表什么？', '[{\"role\": \"user\", \"content\": \"我的检验结果代表什么？\"}, {\"role\": \"assistant\", \"content\": \"根据您的系统记录，最近两次献血（2026年7月4日、5日）的**初筛结果均为合格**。初筛合格代表您在现场通过健康询问和初步血液检测，血液可以进入后续的复检流程。\\n\\n目前系统暂未显示复检结果，因此血液是否最终合格、能否入库，还需要等待复检完成。您可以根据血站通知或后续查看系统更新来确认最终状态。\\n\\n如果您对检验结果有疑问，或想了解具体项目含义，建议直接咨询血站工作人员。\"}, {\"role\": \"user\", \"content\": \"献血前需要注意什么？\"}, {\"role\": \"assistant\", \"content\": \"献血前主要注意以下几点：\\n\\n- **饮食**：献血前一餐不要空腹，建议吃清淡食物（如稀饭、馒头），避免油腻、高蛋白食物（如油条、鸡蛋、豆浆）。\\n- **作息**：保证充足睡眠，不要熬夜，献血前一晚应休息好。\\n- **身体状况**：无发热、感冒、腹泻等不适，近期未服用影响血液的药物（如阿司匹林等）。\\n- **其他**：献血前不要饮酒，多喝水；穿着宽松舒适的衣物，方便暴露手臂。\\n\\n您目前的预约记录显示**2026年7月5日**上午有一场已预约的献血活动（状态：待参加），如果您是准备参加这场活动，可以按以上建议做好准备。如果有其他具体问题，欢迎继续提问！\"}]', '2026-07-06 17:05:47', '2026-07-06 17:06:02', 0);
INSERT INTO `ai_chat_session` VALUES (2074057514076160001, 3, '我该如何预约献血活动？', '[{\"role\": \"user\", \"content\": \"我该如何预约献血活动？\"}, {\"role\": \"assistant\", \"content\": \"当然，以下是预约献血活动的具体步骤：\\n\\n1. **登录系统**：进入系统后，先登录你的账号。\\n2. **完善个人档案**：在用户中心确保个人档案信息（如姓名、身份证号、血型、联系电话等）已填写完整。\\n3. **进入预约管理**：在“预约管理”中查看当前可预约的献血活动。\\n4. **选择活动与时段**：选择你感兴趣的活动，并预约**上午**或**下午**时段。\\n5. **提交预约**：确认信息后提交，之后可在“我的预约”中查看预约状态。\\n\\n根据系统记录，你已有最近的献血记录和预约。如果遇到任何问题，可以随时告诉我。\"}]', '2026-07-06 17:07:00', '2026-07-06 17:07:06', 0);
INSERT INTO `ai_chat_session` VALUES (2074798728085884929, 3, '根据我的记录，我现在还能献血吗？', '[{\"role\": \"user\", \"content\": \"根据我的记录，我现在还能献血吗？\"}, {\"role\": \"assistant\", \"content\": \"根据你的系统记录，我来帮你分析一下：\\n\\n**结论**：系统初步判断你当前满足全血资格与间隔要求，但实际记录中存在明显不一致，**建议暂缓献血，以现场医护判断为准**。\\n\\n**依据**：\\n- 你的系统档案中“最近一次献血日期”显示为**暂无记录**，系统据此判断资格为“满足”。\\n- 但你的**献血记录**显示：\\n  - 2026-07-04：全血 200ml（初筛合格）\\n  - 2026-07-05：全血 400ml（初筛合格）\\n  - 间隔仅1天，且距离今天（2026-07-08）不足6个月，**不符合全血间隔不少于6个月的规则**。\\n- 另外，档案中年龄显示为“1”，这可能属于数据异常，也会影响实际资格判断。\\n\\n**建议**：\\n- 请先联系血站或工作人员，**核实并修正你的个人档案**（年龄、最近一次献血日期等）。\\n- 目前你有两笔待参加的预约（7月10日、7月16日），建议暂缓到场，待档案信息准确后重新评估。\\n\\n**提醒**：\\n- 最终是否适合献血，**请以现场医护或血站工作人员判断为准**。系统记录可能存在延迟或录入错误，请不要仅凭当前记录直接参与献血。\"}]', '2026-07-08 18:12:19', '2026-07-08 18:12:29', 0);

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
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
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `activity_id`(`activity_id`) USING BTREE,
  CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`activity_id`) REFERENCES `blood_activity` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2074430000000000109 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '献血预约记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (2073353454604017666, 3, 1, '上午', '待参加', '2026-07-04 18:29:19', NULL, '2026-07-04 18:29:19', '2026-07-04 18:29:19', 0);
INSERT INTO `appointment` VALUES (2074427830979883010, 3, 2072882713903288324, '上午', '待参加', '2026-07-07 17:38:31', NULL, '2026-07-07 17:38:30', '2026-07-07 17:38:30', 0);
INSERT INTO `appointment` VALUES (2074430000000000101, 2074430000000000002, 2072882713903288327, '上午', '待参加', '2026-07-06 09:12:00', NULL, '2026-07-06 09:12:00', '2026-07-06 09:12:00', 0);
INSERT INTO `appointment` VALUES (2074430000000000102, 2074430000000000003, 2072882713903288327, '下午', '待参加', '2026-07-06 10:25:00', NULL, '2026-07-06 10:25:00', '2026-07-06 10:25:00', 0);
INSERT INTO `appointment` VALUES (2074430000000000103, 2074430000000000004, 2072882713903288328, '上午', '已完成', '2026-07-06 15:40:00', NULL, '2026-07-06 15:40:00', '2026-07-07 10:30:00', 0);
INSERT INTO `appointment` VALUES (2074430000000000104, 2074430000000000005, 2072882713903288328, '下午', '已取消', '2026-07-06 16:10:00', '2026-07-07 08:20:00', '2026-07-06 16:10:00', '2026-07-07 08:20:00', 0);
INSERT INTO `appointment` VALUES (2074430000000000105, 3, 2072882713903288329, '上午', '已完成', '2026-07-01 08:30:00', NULL, '2026-07-01 08:30:00', '2026-07-02 12:00:00', 0);
INSERT INTO `appointment` VALUES (2074430000000000106, 2074430000000000006, 2072882713903288329, '下午', '已失效', '2026-07-01 09:45:00', NULL, '2026-07-01 09:45:00', '2026-07-02 18:00:00', 0);
INSERT INTO `appointment` VALUES (2074430000000000107, 2074430000000000002, 2072882713903288330, '上午', '待参加', '2026-07-07 08:50:00', NULL, '2026-07-07 08:50:00', '2026-07-07 08:50:00', 0);
INSERT INTO `appointment` VALUES (2074430000000000108, 2074430000000000003, 2072882713903288330, '下午', '待参加', '2026-07-07 10:15:00', NULL, '2026-07-07 10:15:00', '2026-07-07 10:15:00', 0);

-- ----------------------------
-- Table structure for blood_activity
-- ----------------------------
DROP TABLE IF EXISTS `blood_activity`;
CREATE TABLE `blood_activity`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 2072882713903288331 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '献血招募活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blood_activity
-- ----------------------------
INSERT INTO `blood_activity` VALUES (1, '市中心爱心献血屋日常采血', '张店区中心血站一楼大厅', '2026-07-10', 49, 50, 48, 50, '已结束', '2026-07-04 18:04:37', '2026-07-13 18:50:52', 0);
INSERT INTO `blood_activity` VALUES (2, '1', '1', '2026-07-01', 50, 50, 50, 50, '未开始', '2026-07-04 21:47:48', '2026-07-05 11:37:36', 1);
INSERT INTO `blood_activity` VALUES (2072882713903288322, '高校校园无偿献血活动', '山东理工大学大学生艺术广场', '2026-07-05', 49, 50, 49, 50, '未开始', '2026-07-06 20:40:18', '2026-07-06 21:49:59', 1);
INSERT INTO `blood_activity` VALUES (2072882713903288323, '献血屋日常采血', '中心血站一楼大厅', '2026-07-13', 50, 50, 50, 50, '未开始', '2026-07-06 20:42:38', '2026-07-06 20:42:38', 0);
INSERT INTO `blood_activity` VALUES (2072882713903288324, '高校校园无偿献血活动', '山东理工大学大学生艺术广场', '2026-07-16', 50, 50, 49, 50, '未开始', '2026-07-06 21:50:20', '2026-07-07 17:38:30', 0);
INSERT INTO `blood_activity` VALUES (2072882713903288325, '7-7活动测试', '山东理工大学大学生艺术广场', '2026-07-05', 10, 10, 10, 10, '未开始', '2026-07-07 10:18:43', '2026-07-07 10:18:43', 0);
INSERT INTO `blood_activity` VALUES (2072882713903288326, '7-7活动测试2', '山东理工大学大学生艺术广场', '2026-07-05', 10, 10, 10, 10, '未开始', '2026-07-07 13:28:14', '2026-07-07 13:28:14', 0);
INSERT INTO `blood_activity` VALUES (2072882713903288327, '社区无偿献血公益活动', '张店区人民公园南门广场', '2026-07-08', 30, 30, 28, 30, '未开始', '2026-07-05 09:00:00', '2026-07-07 10:00:00', 0);
INSERT INTO `blood_activity` VALUES (2072882713903288328, '企业职工无偿献血专场', '齐鲁石化公司职工活动中心', '2026-07-07', 40, 40, 15, 22, '已结束', '2026-07-05 14:30:00', '2026-07-10 09:13:06', 0);
INSERT INTO `blood_activity` VALUES (2072882713903288329, '乡镇流动采血车活动', '淄川区昆仑镇政府大院', '2026-07-02', 25, 25, 0, 0, '已结束', '2026-06-28 10:00:00', '2026-07-02 18:00:00', 0);
INSERT INTO `blood_activity` VALUES (2072882713903288330, '夏季应急献血招募活动', '市中心血站二楼采血大厅', '2026-07-20', 60, 60, 60, 60, '未开始', '2026-07-06 16:00:00', '2026-07-06 16:00:00', 0);

-- ----------------------------
-- Table structure for blood_collection
-- ----------------------------
DROP TABLE IF EXISTS `blood_collection`;
CREATE TABLE `blood_collection`  (
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
  INDEX `donor_id`(`donor_id`) USING BTREE,
  INDEX `operator_id`(`operator_id`) USING BTREE,
  CONSTRAINT `blood_collection_ibfk_1` FOREIGN KEY (`donor_id`) REFERENCES `donor` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `blood_collection_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2076923080080113666 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '采血记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blood_collection
-- ----------------------------
INSERT INTO `blood_collection` VALUES (2073413193044176897, 2, '370781200410283311', 200, '全血', '合格', '2026-07-04 22:26:42', 1, '2026-07-04 22:26:42', '2026-07-04 22:26:42', 0);
INSERT INTO `blood_collection` VALUES (2073617161636544513, 2, '370781200410283311', 400, '全血', '合格', '2026-07-05 11:57:12', 1, '2026-07-05 11:57:11', '2026-07-05 11:57:11', 0);
INSERT INTO `blood_collection` VALUES (2073638933392011265, 6, 'TFeQVarQH1cEvNLpFj87/9tlSqoL26kTZwOkbg4fNwg=', 200, '全血', '合格', '2026-07-05 13:23:43', 1, '2026-07-05 13:23:42', '2026-07-05 13:23:42', 0);
INSERT INTO `blood_collection` VALUES (2074098888536272898, 7, 'DW+fU9BddIcL6Bpm1fG7vNtlSqoL26kTZwOkbg4fNwg=', 200, '全血', '合格', '2026-07-06 19:51:25', 2, '2026-07-06 19:51:24', '2026-07-06 19:51:24', 0);
INSERT INTO `blood_collection` VALUES (2074409391053664257, 6, 'TFeQVarQH1cEvNLpFj87/9tlSqoL26kTZwOkbg4fNwg=', 220, '全血', '合格', '2026-07-07 16:25:14', 2, '2026-07-07 16:25:14', '2026-07-07 16:25:14', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000201, 12, 'aBcDeF1234567890abcdefghijklmnopqrstuvwxyz123=', 300, '全血', '合格', '2026-07-07 09:15:00', 2074430000000000001, '2026-07-07 09:15:00', '2026-07-07 09:15:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000202, 13, 'bCdEfG2345678901bcdefghijklmnopqrstuvwxyz234=', 400, '全血', '合格', '2026-07-07 09:40:00', 2074430000000000001, '2026-07-07 09:40:00', '2026-07-07 09:40:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000203, 14, 'cDeFgH3456789012cdefghijklmnopqrstuvwxyz345=', 200, '全血', '合格', '2026-07-07 10:05:00', 2074430000000000001, '2026-07-07 10:05:00', '2026-07-07 10:05:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000204, 15, 'dEfGhI4567890123defghijklmnopqrstuvwxyz456=', 200, '成分血', '合格', '2026-07-07 10:30:00', 2074430000000000001, '2026-07-07 10:30:00', '2026-07-07 10:30:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000205, 17, 'fGhIjK6789012345fghijklmnopqrstuvwxyz678=', 400, '全血', '合格', '2026-07-07 10:55:00', 2074430000000000001, '2026-07-07 10:55:00', '2026-07-07 10:55:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000206, 19, 'hIjKlM8901234567hijklmnopqrstuvwxyz890=', 300, '全血', '不合格', '2026-07-07 11:20:00', 2074430000000000001, '2026-07-07 11:20:00', '2026-07-07 11:20:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000207, 21, 'jKlMnO0123456789jklmnopqrstuvwxyz012=', 400, '全血', '合格', '2026-07-07 14:10:00', 2074430000000000001, '2026-07-07 14:10:00', '2026-07-07 14:10:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000208, 23, 'lMnOpQ2345678901lmnopqrstuvwxyz234=', 200, '全血', '合格', '2026-07-07 14:35:00', 2074430000000000001, '2026-07-07 14:35:00', '2026-07-07 14:35:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000209, 12, 'aBcDeF1234567890abcdefghijklmnopqrstuvwxyz123=', 400, '全血', '合格', '2026-07-02 09:20:00', 1, '2026-07-02 09:20:00', '2026-07-02 09:20:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000210, 17, 'fGhIjK6789012345fghijklmnopqrstuvwxyz678=', 200, '全血', '合格', '2026-07-02 10:05:00', 1, '2026-07-02 10:05:00', '2026-07-02 10:05:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000211, 13, 'bCdEfG2345678901bcdefghijklmnopqrstuvwxyz234=', 300, '全血', '合格', '2026-07-02 14:30:00', 1, '2026-07-02 14:30:00', '2026-07-02 14:30:00', 0);
INSERT INTO `blood_collection` VALUES (2074430000000000212, 20, 'iJkLmN9012345678ijklmnopqrstuvwxyz901=', 200, '成分血', '合格', '2026-07-02 15:10:00', 1, '2026-07-02 15:10:00', '2026-07-02 15:10:00', 0);
INSERT INTO `blood_collection` VALUES (2074491884175978498, 10, 'CSpralndYFq55Y6A7lUr2G7SzzuMJ0VKNDhtRXCV4Eo=', 200, '全血', '合格', '2026-07-07 21:53:02', 2, '2026-07-07 21:53:02', '2026-07-07 21:53:02', 0);
INSERT INTO `blood_collection` VALUES (2074809192337436673, 16, 'eFgHiJ5678901234efghijklmnopqrstuvwxyz567=', 200, '全血', '合格', '2026-07-08 18:53:54', 2, '2026-07-08 18:53:54', '2026-07-08 18:53:54', 0);
INSERT INTO `blood_collection` VALUES (2075387975498997761, 18, 'gHiJkL7890123456ghijklmnopqrstuvwxyz789=', 400, '全血', '合格', '2026-07-10 09:13:47', 2, '2026-07-10 09:13:46', '2026-07-10 09:13:46', 0);
INSERT INTO `blood_collection` VALUES (2075402018251698178, 11, 'Ngo6pJEPxqijfLAYjcI/r33sTe6ZW/b52Wk7rF/yn2k=', 400, '全血', '合格', '2026-07-10 10:09:35', 2, '2026-07-10 10:09:34', '2026-07-10 10:09:34', 0);
INSERT INTO `blood_collection` VALUES (2076601637974917122, 3, 'K67cL1djGmDfCc3++I05tu6jY9XxVAq2p7XhfdrnWCQ=', 400, '全血', '合格', '2026-07-13 17:36:27', 2, '2026-07-13 17:36:26', '2026-07-13 18:35:24', 1);
INSERT INTO `blood_collection` VALUES (2076616534796288002, 3, 'K67cL1djGmDfCc3++I05tu6jY9XxVAq2p7XhfdrnWCQ=', 400, '全血', '合格', '2026-07-13 18:35:38', 2, '2026-07-13 18:35:38', '2026-07-13 18:35:38', 0);
INSERT INTO `blood_collection` VALUES (2076620569599369217, 2, 'TFeQVarQH1cEvNLpFj87/zs9UMkIvVHepWrGrmovRuM=', 400, '全血', '合格', '2026-07-13 18:51:40', 2, '2026-07-13 18:51:40', '2026-07-13 18:51:40', 0);
INSERT INTO `blood_collection` VALUES (2076620670229110786, 7, 'DW+fU9BddIcL6Bpm1fG7vNtlSqoL26kTZwOkbg4fNwg=', 400, '全血', '合格', '2026-07-13 18:52:04', 2, '2026-07-13 18:52:04', '2026-07-13 18:52:04', 0);
INSERT INTO `blood_collection` VALUES (2076923080080113665, 22, 'kLmNoP1234567890klmnopqrstuvwxyz123=', 200, '全血', '合格', '2026-07-14 14:53:44', 2, '2026-07-14 14:53:44', '2026-07-14 14:53:44', 0);
INSERT INTO `blood_collection` VALUES (2076973945692450818, 8, 'zljUu92TE0WJMLOTibjX6+SZB7hURFW8jDsDUAnoUsg=', 400, '全血', '合格', '2026-07-14 18:15:52', 2, '2026-07-14 18:15:51', '2026-07-14 18:15:51', 0);
INSERT INTO `blood_collection` VALUES (2076974169806696450, 9, 'qajTYXK001aWpUEPOMtB7afKfTZu0chWcmwh+SqMmOM=', 400, '全血', '合格', '2026-07-14 18:16:45', 2, '2026-07-14 18:16:45', '2026-07-14 18:16:45', 0);
INSERT INTO `blood_collection` VALUES (2077004583749382146, 14, 'cDeFgH3456789012cdefghijklmnopqrstuvwxyz345=', 400, '全血', '合格', '2026-07-14 20:17:36', 2, '2026-07-14 20:17:36', '2026-07-14 20:17:36', 0);

-- ----------------------------
-- Table structure for blood_stock
-- ----------------------------
DROP TABLE IF EXISTS `blood_stock`;
CREATE TABLE `blood_stock`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存明细ID',
  `collection_id` bigint NOT NULL COMMENT '关联采血记录ID',
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '血型',
  `blood_amount` int NOT NULL COMMENT '血量ml',
  `expire_date` date NOT NULL COMMENT '血液有效期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '正常' COMMENT '库存状态：正常/临期/已过期/已出库',
  `out_unit` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用血单位',
  `out_purpose` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '血液用途',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_collection_id`(`collection_id`) USING BTREE,
  CONSTRAINT `blood_stock_ibfk_1` FOREIGN KEY (`collection_id`) REFERENCES `blood_collection` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '血液库存明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blood_stock
-- ----------------------------
INSERT INTO `blood_stock` VALUES (1, 2073617161636544513, 'B型', 400, '2026-08-11', '已出库', '渗析', '治疗性血液置换', '2026-07-07 15:02:43', '2026-07-07 15:02:43', 0);
INSERT INTO `blood_stock` VALUES (2, 2073638933392011265, 'O型', 200, '2026-08-11', '正常', NULL, NULL, '2026-07-07 15:02:43', '2026-07-07 15:02:43', 0);
INSERT INTO `blood_stock` VALUES (3, 2074430000000000201, 'A型', 300, '2026-09-07', '正常', NULL, NULL, '2026-07-07 15:30:00', '2026-07-07 15:30:00', 0);
INSERT INTO `blood_stock` VALUES (4, 2074430000000000209, 'A型', 400, '2026-09-02', '正常', NULL, NULL, '2026-07-03 10:00:00', '2026-07-03 10:00:00', 0);
INSERT INTO `blood_stock` VALUES (5, 2074430000000000202, 'B型', 400, '2026-09-07', '正常', NULL, NULL, '2026-07-07 15:35:00', '2026-07-07 15:35:00', 0);
INSERT INTO `blood_stock` VALUES (6, 2074430000000000211, 'B型', 300, '2026-09-02', '已出库', '过期处理', '科研教学与质控', '2026-07-03 10:30:00', '2026-07-06 18:00:00', 0);
INSERT INTO `blood_stock` VALUES (7, 2074430000000000203, 'O型', 200, '2026-09-07', '正常', NULL, NULL, '2026-07-07 10:05:00', '2026-07-07 10:05:00', 0);
INSERT INTO `blood_stock` VALUES (8, 2074430000000000210, 'O型', 200, '2026-09-02', '已出库', '淄博市中心医院', '临床手术用血', '2026-07-03 10:15:00', '2026-07-05 09:30:00', 0);
INSERT INTO `blood_stock` VALUES (9, 2074430000000000204, 'AB型', 200, '2026-09-07', '正常', NULL, NULL, '2026-07-07 10:30:00', '2026-07-07 10:30:00', 0);
INSERT INTO `blood_stock` VALUES (10, 2074430000000000212, 'AB型', 200, '2026-09-02', '临期', NULL, NULL, '2026-07-03 10:45:00', '2026-07-07 08:00:00', 0);
INSERT INTO `blood_stock` VALUES (11, 2073413193044176897, 'B型', 200, '2026-08-04', '正常', NULL, NULL, '2026-07-07 16:24:00', '2026-07-07 16:24:00', 0);
INSERT INTO `blood_stock` VALUES (12, 2074409391053664257, 'O型', 220, '2026-09-07', '正常', NULL, NULL, '2026-07-07 16:26:00', '2026-07-07 16:26:00', 0);
INSERT INTO `blood_stock` VALUES (13, 2075387975498997761, 'B型', 400, '2026-08-14', '已出库', '淄博市中心医院', '临床手术用血', '2026-07-10 09:15:43', '2026-07-10 09:15:43', 0);
INSERT INTO `blood_stock` VALUES (14, 2076974169806696450, 'B型', 400, '2026-08-18', '已出库', '淄博市市中心医院', '危重患者急救', '2026-07-14 18:19:36', '2026-07-14 18:19:36', 0);

-- ----------------------------
-- Table structure for blood_test
-- ----------------------------
DROP TABLE IF EXISTS `blood_test`;
CREATE TABLE `blood_test`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '检验记录ID',
  `collection_id` bigint NOT NULL COMMENT '关联采血记录ID',
  `donor_id` bigint NOT NULL COMMENT '献血者档案ID',
  `recheck_result` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '复检详细结果',
  `blood_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '待检验' COMMENT '血液状态：待检验/合格/不合格/已入库',
  `unqualified_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '不合格原因',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `judge_time` datetime NULL DEFAULT NULL COMMENT '判定时间',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '判定管理员ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检验批次号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_collection_id`(`collection_id`) USING BTREE,
  INDEX `donor_id`(`donor_id`) USING BTREE,
  INDEX `operator_id`(`operator_id`) USING BTREE,
  CONSTRAINT `blood_test_ibfk_1` FOREIGN KEY (`collection_id`) REFERENCES `blood_collection` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `blood_test_ibfk_2` FOREIGN KEY (`donor_id`) REFERENCES `donor` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `blood_test_ibfk_3` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2076923080134639618 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '血液检验记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blood_test
-- ----------------------------
INSERT INTO `blood_test` VALUES (2073413193094508545, 2073413193044176897, 2, '合格', '已入库', NULL, '', '2026-07-07 16:24:07', 2, NULL, '2026-07-04 22:26:42', '2026-07-08 18:48:22', 0);
INSERT INTO `blood_test` VALUES (2073617161674293249, 2073617161636544513, 2, '合格', '已入库', NULL, '', '2026-07-07 15:02:24', 2, NULL, '2026-07-05 11:57:11', '2026-07-05 11:57:11', 0);
INSERT INTO `blood_test` VALUES (2073638933475897345, 2073638933392011265, 6, '合格', '已入库', NULL, '', '2026-07-07 15:02:14', 2, NULL, '2026-07-05 13:23:42', '2026-07-05 13:23:42', 0);
INSERT INTO `blood_test` VALUES (2074098888557244417, 2074098888536272898, 7, '谷丙转氨酶（ALT）超标', '不合格', '谷丙转氨酶偏高', '', '2026-07-07 22:22:20', 2, NULL, '2026-07-06 19:51:24', '2026-07-06 19:51:24', 0);
INSERT INTO `blood_test` VALUES (2074409391053664258, 2074409391053664257, 6, '合格', '已入库', NULL, '', '2026-07-07 16:26:09', 2, NULL, '2026-07-07 16:25:14', '2026-07-08 18:48:22', 0);
INSERT INTO `blood_test` VALUES (2074430000000000301, 2074430000000000201, 12, '各项指标均合格', '已入库', NULL, '复检完成，合格入库', '2026-07-07 15:30:00', 2, NULL, '2026-07-07 09:15:00', '2026-07-07 15:30:00', 0);
INSERT INTO `blood_test` VALUES (2074430000000000302, 2074430000000000202, 13, '各项指标均合格', '已入库', NULL, '待入库', '2026-07-07 15:35:00', 2, NULL, '2026-07-07 09:40:00', '2026-07-08 18:48:22', 0);
INSERT INTO `blood_test` VALUES (2074430000000000303, 2074430000000000203, 14, '合格', '已入库', NULL, '刚采血完成，待实验室复检', '2026-07-07 22:21:35', 2, NULL, '2026-07-07 10:05:00', '2026-07-08 18:48:22', 0);
INSERT INTO `blood_test` VALUES (2074430000000000304, 2074430000000000204, 15, '合格', '已入库', NULL, '成分血待分离检验', '2026-07-07 17:57:38', 2, NULL, '2026-07-07 10:30:00', '2026-07-08 18:48:22', 0);
INSERT INTO `blood_test` VALUES (2074430000000000305, 2074430000000000205, 17, '谷丙转氨酶（ALT）超标', '不合格', '转氨酶升高（ALT: 68U/L）', '建议休息后复查', '2026-07-07 22:21:15', 2, NULL, '2026-07-07 10:55:00', '2026-07-07 15:40:00', 0);
INSERT INTO `blood_test` VALUES (2074430000000000306, 2074430000000000206, 19, '血红蛋白常规指标不合格', '不合格', '初筛贫血，未进入复检流程', '血红蛋白低于标准，暂缓献血', '2026-07-07 22:21:09', 2, NULL, '2026-07-07 11:20:00', '2026-07-07 11:25:00', 0);
INSERT INTO `blood_test` VALUES (2074430000000000307, 2074430000000000207, 21, '血液传染病指标异常', '不合格', '乙肝表面抗原（HBsAg）阳性', '永久淘汰，已标记重点关注', '2026-07-07 22:20:59', 2, NULL, '2026-07-07 14:10:00', '2026-07-07 16:00:00', 0);
INSERT INTO `blood_test` VALUES (2074430000000000308, 2074430000000000208, 23, '其他', '不合格', '甘油三酯严重偏高，乳糜血', '本年度第2次不合格，已标记重点关注', '2026-07-07 22:20:27', 2, NULL, '2026-07-07 14:35:00', '2026-07-07 16:10:00', 0);
INSERT INTO `blood_test` VALUES (2074430000000000309, 2074430000000000209, 12, '各项指标均合格', '已入库', NULL, '历史采血记录，已入库', '2026-07-03 10:00:00', 1, NULL, '2026-07-02 09:20:00', '2026-07-03 10:00:00', 0);
INSERT INTO `blood_test` VALUES (2074430000000000310, 2074430000000000210, 17, '各项指标均合格', '已出库', NULL, '已发往临床使用', '2026-07-03 10:15:00', 1, NULL, '2026-07-02 10:05:00', '2026-07-05 09:30:00', 0);
INSERT INTO `blood_test` VALUES (2074430000000000311, 2074430000000000211, 13, '各项指标均合格', '已过期', NULL, '血液已过有效期，报废处理', '2026-07-03 10:30:00', 1, NULL, '2026-07-02 14:30:00', '2026-07-06 18:00:00', 0);
INSERT INTO `blood_test` VALUES (2074430000000000312, 2074430000000000212, 20, '合格', '已入库', NULL, '距离有效期不足7天', '2026-07-07 22:22:31', 2, NULL, '2026-07-02 15:10:00', '2026-07-08 18:48:22', 0);
INSERT INTO `blood_test` VALUES (2074491884339556353, 2074491884175978498, 10, '合格', '合格', NULL, '', '2026-07-08 18:54:15', 2, NULL, '2026-07-07 21:53:02', '2026-07-07 21:53:02', 0);
INSERT INTO `blood_test` VALUES (2074809192484237314, 2074809192337436673, 16, '合格', '合格', NULL, '', '2026-07-08 18:54:05', 2, NULL, '2026-07-08 18:53:54', '2026-07-08 18:53:54', 0);
INSERT INTO `blood_test` VALUES (2075387975561912322, 2075387975498997761, 18, '合格', '已入库', '乙肝病毒阳性', '', '2026-07-10 09:15:30', 2, NULL, '2026-07-10 09:13:46', '2026-07-10 09:13:46', 0);
INSERT INTO `blood_test` VALUES (2075402018616602625, 2075402018251698178, 11, '合格', '合格', NULL, '', '2026-07-10 10:15:59', 2, NULL, '2026-07-10 10:09:35', '2026-07-10 10:09:35', 0);
INSERT INTO `blood_test` VALUES (2076601638121717762, 2076601637974917122, 3, NULL, '待检验', NULL, NULL, NULL, NULL, NULL, '2026-07-13 17:36:26', '2026-07-13 18:35:24', 1);
INSERT INTO `blood_test` VALUES (2076616534863396866, 2076616534796288002, 3, '合格', '合格', NULL, NULL, '2026-07-13 18:43:34', NULL, NULL, '2026-07-13 18:35:38', '2026-07-13 18:35:38', 0);
INSERT INTO `blood_test` VALUES (2076620569666478082, 2076620569599369217, 2, '血液传染病指标异常', '不合格', '艾滋病抗体阳性', NULL, '2026-07-13 18:53:11', NULL, NULL, '2026-07-13 18:51:40', '2026-07-13 18:51:40', 0);
INSERT INTO `blood_test` VALUES (2076620670229110787, 2076620670229110786, 7, '谷丙转氨酶（ALT）超标；血液传染病指标异常；血红蛋白常规指标不合格；血小板计数不合格', '不合格', '谷丙转氨酶（ALT）超标(50.0U/L)；乙肝表面抗原阳性；血红蛋白低于标准(100.0g/L)；血小板计数异常(100.0×10^9/L)', NULL, '2026-07-13 18:52:41', NULL, NULL, '2026-07-13 18:52:04', '2026-07-13 18:52:04', 0);
INSERT INTO `blood_test` VALUES (2076923080134639617, 2076923080080113665, 22, '血红蛋白常规指标不合格', '不合格', '血红蛋白低于标准(100.0g/L)', NULL, '2026-07-14 18:17:39', NULL, NULL, '2026-07-14 14:53:44', '2026-07-14 14:53:44', 0);
INSERT INTO `blood_test` VALUES (2076973945793114114, 2076973945692450818, 8, '血液传染病指标异常', '不合格', '丙肝抗体阳性；乙肝表面抗原阳性', NULL, '2026-07-14 18:18:20', NULL, NULL, '2026-07-14 18:15:51', '2026-07-14 18:15:51', 0);
INSERT INTO `blood_test` VALUES (2076974169873805313, 2076974169806696450, 9, '合格', '已入库', NULL, NULL, '2026-07-14 18:18:46', NULL, NULL, '2026-07-14 18:16:45', '2026-07-14 18:16:45', 0);
INSERT INTO `blood_test` VALUES (2077004583879405570, 2077004583749382146, 14, NULL, '待检验', NULL, NULL, NULL, NULL, NULL, '2026-07-14 20:17:36', '2026-07-14 20:17:36', 0);

-- ----------------------------
-- Table structure for blood_test_indicator
-- ----------------------------
DROP TABLE IF EXISTS `blood_test_indicator`;
CREATE TABLE `blood_test_indicator`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '指标记录ID',
  `test_id` bigint NOT NULL COMMENT '关联检验记录ID',
  `alt` decimal(5, 1) NULL DEFAULT NULL COMMENT '谷丙转氨酶ALT(U/L)',
  `ast` decimal(5, 1) NULL DEFAULT NULL COMMENT '谷草转氨酶AST(U/L)',
  `total_bilirubin` decimal(5, 1) NULL DEFAULT NULL COMMENT '总胆红素(μmol/L)',
  `direct_bilirubin` decimal(5, 1) NULL DEFAULT NULL COMMENT '直接胆红素(μmol/L)',
  `hbv_surface_antigen` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '乙肝表面抗原：阴性/阳性',
  `hbv_surface_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '乙肝表面抗体：阴性/阳性',
  `hbv_e_antigen` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '乙肝e抗原：阴性/阳性',
  `hbv_e_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '乙肝e抗体：阴性/阳性',
  `hbv_core_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '乙肝核心抗体：阴性/阳性',
  `hcv_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '丙肝抗体：阴性/阳性',
  `hiv_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '艾滋病抗体：阴性/阳性',
  `syphilis_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '梅毒抗体：阴性/阳性',
  `white_blood_cell` decimal(5, 2) NULL DEFAULT NULL COMMENT '白细胞计数(×10^9/L)',
  `red_blood_cell` decimal(5, 2) NULL DEFAULT NULL COMMENT '红细胞计数(×10^12/L)',
  `hemoglobin` decimal(5, 1) NULL DEFAULT NULL COMMENT '血红蛋白(g/L)',
  `hematocrit` decimal(5, 1) NULL DEFAULT NULL COMMENT '红细胞压积(%)',
  `mean_cell_volume` decimal(5, 1) NULL DEFAULT NULL COMMENT '平均红细胞体积(fL)',
  `mean_cell_hemoglobin` decimal(5, 1) NULL DEFAULT NULL COMMENT '平均红细胞血红蛋白含量(pg)',
  `mean_cell_hemoglobin_concentration` decimal(5, 1) NULL DEFAULT NULL COMMENT '平均红细胞血红蛋白浓度(g/L)',
  `platelet` decimal(5, 2) NULL DEFAULT NULL COMMENT '血小板计数(×10^9/L)',
  `mean_platelet_volume` decimal(5, 1) NULL DEFAULT NULL COMMENT '平均血小板体积(fL)',
  `glucose` decimal(5, 1) NULL DEFAULT NULL COMMENT '血糖(mmol/L)',
  `creatinine` decimal(5, 1) NULL DEFAULT NULL COMMENT '肌酐(μmol/L)',
  `blood_urea_nitrogen` decimal(5, 1) NULL DEFAULT NULL COMMENT '尿素氮(mmol/L)',
  `cholesterol` decimal(5, 1) NULL DEFAULT NULL COMMENT '总胆固醇(mmol/L)',
  `triglyceride` decimal(5, 1) NULL DEFAULT NULL COMMENT '甘油三酯(mmol/L)',
  `protein` decimal(5, 1) NULL DEFAULT NULL COMMENT '总蛋白(g/L)',
  `albumin` decimal(5, 1) NULL DEFAULT NULL COMMENT '白蛋白(g/L)',
  `globulin` decimal(5, 1) NULL DEFAULT NULL COMMENT '球蛋白(g/L)',
  `potassium` decimal(5, 2) NULL DEFAULT NULL COMMENT '钾(mmol/L)',
  `sodium` decimal(5, 1) NULL DEFAULT NULL COMMENT '钠(mmol/L)',
  `chloride` decimal(5, 1) NULL DEFAULT NULL COMMENT '氯(mmol/L)',
  `calcium` decimal(5, 2) NULL DEFAULT NULL COMMENT '钙(mmol/L)',
  `iron` decimal(5, 1) NULL DEFAULT NULL COMMENT '铁(μmol/L)',
  `ferritin` decimal(7, 1) NULL DEFAULT NULL COMMENT '铁蛋白(ng/mL)',
  `other_abnormality` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '其他异常情况',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blood_test_indicator_test_id`(`test_id`) USING BTREE,
  CONSTRAINT `blood_test_indicator_ibfk_1` FOREIGN KEY (`test_id`) REFERENCES `blood_test` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2076620951914373123 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '血液检验指标表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blood_test_indicator
-- ----------------------------
INSERT INTO `blood_test_indicator` VALUES (2076618530869829633, 2076616534863396866, 35.0, NULL, NULL, NULL, '阴性', NULL, NULL, NULL, NULL, '阴性', '阴性', '阴性', 6.00, NULL, 130.0, NULL, NULL, NULL, NULL, 200.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-13 18:43:34', '2026-07-13 18:43:34', 0);
INSERT INTO `blood_test_indicator` VALUES (2076620825040871425, 2076620670229110787, 50.0, NULL, NULL, NULL, '阳性', NULL, NULL, NULL, NULL, '阴性', '阴性', '阴性', 4.00, NULL, 100.0, NULL, NULL, NULL, NULL, 100.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-13 18:52:41', '2026-07-13 18:52:41', 0);
INSERT INTO `blood_test_indicator` VALUES (2076620951914373122, 2076620569666478082, 20.0, NULL, NULL, NULL, '阴性', NULL, NULL, NULL, NULL, '阴性', '阳性', '阴性', 4.00, NULL, 130.0, NULL, NULL, NULL, NULL, 130.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-13 18:53:11', '2026-07-13 18:53:11', 0);
INSERT INTO `blood_test_indicator` VALUES (2076974395531554818, 2076923080134639617, 12.0, NULL, NULL, NULL, '阴性', NULL, NULL, NULL, NULL, '阴性', '阴性', NULL, 4.00, NULL, 100.0, NULL, NULL, NULL, NULL, 200.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-14 18:17:38', '2026-07-14 18:17:38', 0);
INSERT INTO `blood_test_indicator` VALUES (2076974566218756098, 2076973945793114114, 20.0, NULL, NULL, NULL, '阳性', NULL, NULL, NULL, NULL, '阳性', '阴性', '阴性', 5.00, NULL, 132.0, NULL, NULL, NULL, NULL, 200.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-14 18:18:19', '2026-07-14 18:18:19', 0);
INSERT INTO `blood_test_indicator` VALUES (2076974676277293058, 2076974169873805313, 20.0, NULL, NULL, NULL, '阴性', NULL, NULL, NULL, NULL, '阴性', '阴性', '阴性', 5.00, NULL, 200.0, NULL, NULL, NULL, NULL, 200.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-07-14 18:18:45', '2026-07-14 18:18:45', 0);

-- ----------------------------
-- Table structure for donor
-- ----------------------------
DROP TABLE IF EXISTS `donor`;
CREATE TABLE `donor`  (
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
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `donor_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '献血者档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of donor
-- ----------------------------
INSERT INTO `donor` VALUES (2, 3, '李四', 'TFeQVarQH1cEvNLpFj87/zs9UMkIvVHepWrGrmovRuM=', 'B型', '13465363301', 'q9U8w8aJYr9WTAdbh0MY9w==', '正常', '2026-07-13', '2026-07-04 18:50:18', '2026-07-06 19:38:30', 0, '男', 1, '3', 0);
INSERT INTO `donor` VALUES (3, 1, '张三', 'K67cL1djGmDfCc3++I05tu6jY9XxVAq2p7XhfdrnWCQ=', 'A型', '13465363302', 'LCMNVWqc8NRlWX4Dl0fqZA==', '正常', '2026-07-13', '2026-07-04 19:03:40', '2026-07-06 19:38:34', 0, '男', 1, '山东省淄博市张店区', 0);
INSERT INTO `donor` VALUES (6, NULL, '王文武', 'TFeQVarQH1cEvNLpFj87/9tlSqoL26kTZwOkbg4fNwg=', 'O型', '13465363302', 'RCDAr6v4nY/rkpH0Yy880g==', '正常', NULL, '2026-07-05 11:54:04', '2026-07-06 19:38:40', 0, '男', 4, '4', 0);
INSERT INTO `donor` VALUES (7, NULL, '王五', 'DW+fU9BddIcL6Bpm1fG7vNtlSqoL26kTZwOkbg4fNwg=', 'O型', '15863904119', NULL, '正常', '2026-07-13', '2026-07-06 19:44:08', '2026-07-06 19:44:08', 0, '男', 21, '山东理工大学', 0);
INSERT INTO `donor` VALUES (8, NULL, '李一', 'zljUu92TE0WJMLOTibjX6+SZB7hURFW8jDsDUAnoUsg=', 'B型', '15863904119', NULL, '正常', '2026-07-14', '2026-07-06 20:02:52', '2026-07-06 20:02:52', 0, '男', 36, '山东理工大学', 0);
INSERT INTO `donor` VALUES (9, NULL, '李二', 'qajTYXK001aWpUEPOMtB7afKfTZu0chWcmwh+SqMmOM=', 'B型', '19819151223', NULL, '正常', '2026-07-14', '2026-07-06 20:11:54', '2026-07-06 20:11:54', 0, '女', 29, '淄博淄川', 0);
INSERT INTO `donor` VALUES (10, NULL, '张二', 'CSpralndYFq55Y6A7lUr2G7SzzuMJ0VKNDhtRXCV4Eo=', 'A型', '19819151223', NULL, '正常', NULL, '2026-07-06 20:13:25', '2026-07-06 20:13:25', 0, '男', 37, '山东理工大学', 0);
INSERT INTO `donor` VALUES (11, NULL, '钱六', 'Ngo6pJEPxqijfLAYjcI/r33sTe6ZW/b52Wk7rF/yn2k=', 'AB型', '19819151223', NULL, '正常', '2026-07-10', '2026-07-06 20:14:01', '2026-07-06 20:14:01', 0, '女', 28, '山东理工大学', 0);
INSERT INTO `donor` VALUES (12, 2074430000000000002, '陈晓燕', 'aBcDeF1234567890abcdefghijklmnopqrstuvwxyz123=', 'A型', '13561678821', NULL, '正常', '2026-03-15', '2026-07-02 10:15:00', '2026-07-02 10:15:00', 0, '女', 26, '淄博市张店区共青团西路', 0);
INSERT INTO `donor` VALUES (13, 2074430000000000003, '赵建国', 'bCdEfG2345678901bcdefghijklmnopqrstuvwxyz234=', 'B型', '13678654432', NULL, '正常', '2026-01-20', '2026-07-02 14:20:00', '2026-07-02 14:20:00', 0, '男', 42, '淄博市淄川区松龄路街道', 0);
INSERT INTO `donor` VALUES (14, 2074430000000000004, '孙梦琪', 'cDeFgH3456789012cdefghijklmnopqrstuvwxyz345=', 'O型', '13853369901', NULL, '正常', '2026-07-14', '2026-07-03 08:45:00', '2026-07-03 08:45:00', 0, '女', 22, '山东理工大学西校区', 0);
INSERT INTO `donor` VALUES (15, 2074430000000000005, '周浩然', 'dEfGhI4567890123defghijklmnopqrstuvwxyz456=', 'AB型', '15069327788', NULL, '正常', '2025-11-08', '2026-07-03 16:30:00', '2026-07-03 16:30:00', 0, '男', 31, '淄博市临淄区桓公路', 0);
INSERT INTO `donor` VALUES (16, 2074430000000000006, '吴秀兰', 'eFgHiJ5678901234efghijklmnopqrstuvwxyz567=', 'O型', '13573356612', '高血压病史', '暂缓', '2026-07-08', '2026-07-04 11:00:00', '2026-07-04 11:00:00', 0, '女', 48, '淄博市博山区城东街道', 0);
INSERT INTO `donor` VALUES (17, NULL, '郑海涛', 'fGhIjK6789012345fghijklmnopqrstuvwxyz678=', 'A型', '13964385523', NULL, '正常', '2026-02-28', '2026-07-04 09:30:00', '2026-07-04 09:30:00', 0, '男', 35, '淄博市周村区青年路', 0);
INSERT INTO `donor` VALUES (18, NULL, '王雅婷', 'gHiJkL7890123456ghijklmnopqrstuvwxyz789=', 'B型', '18766994432', NULL, '永久淘汰', '2026-07-10', '2026-07-04 10:15:00', '2026-07-04 10:15:00', 0, '女', 29, '淄博市张店区高新区', 1);
INSERT INTO `donor` VALUES (19, NULL, '刘志强', 'hIjKlM8901234567hijklmnopqrstuvwxyz890=', 'O型', '13583372201', NULL, '正常', '2026-04-12', '2026-07-05 08:20:00', '2026-07-05 08:20:00', 0, '男', 38, '淄博市桓台县索镇街道', 0);
INSERT INTO `donor` VALUES (20, NULL, '张敏', 'iJkLmN9012345678ijklmnopqrstuvwxyz901=', 'AB型', '15965528877', NULL, '正常', NULL, '2026-07-05 13:40:00', '2026-07-05 13:40:00', 0, '女', 24, '山东理工大学东校区', 0);
INSERT INTO `donor` VALUES (21, NULL, '陈磊', 'jKlMnO0123456789jklmnopqrstuvwxyz012=', 'A型', '13626439956', NULL, '正常', '2025-10-18', '2026-07-05 15:10:00', '2026-07-05 15:10:00', 0, '男', 45, '淄博市沂源县城区', 0);
INSERT INTO `donor` VALUES (22, NULL, '李娜', 'kLmNoP1234567890klmnopqrstuvwxyz123=', 'O型', '15069364421', NULL, '暂缓', '2026-07-14', '2026-07-06 10:05:00', '2026-07-06 10:05:00', 0, '女', 33, '淄博市高青县田镇街道', 0);
INSERT INTO `donor` VALUES (23, NULL, '黄勇', 'lMnOpQ2345678901lmnopqrstuvwxyz234=', 'B型', '13864417789', NULL, '正常', '2026-06-01', '2026-07-06 14:30:00', '2026-07-06 14:30:00', 0, '男', 52, '淄博市张店区南定镇', 1);

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人姓名',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型：新增/修改/删除/导出等',
  `operation_content` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作内容详情',
  `operation_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `operator_id`(`operator_id`) USING BTREE,
  INDEX `operation_time`(`operation_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2076923080197554178 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 2, '超级管理员', '新增', '新增献血活动：高校校园无偿献血活动', '2026-07-06 20:40:18', '127.0.0.1');
INSERT INTO `operation_log` VALUES (2, 2, '超级管理员', '修改', '修改活动：市中心爱心献血屋日常采血，调整上午名额', '2026-07-06 20:40:45', '127.0.0.1');
INSERT INTO `operation_log` VALUES (3, 1, '管理员', '新增', '录入献血者档案：王文武', '2026-07-05 11:54:04', '127.0.0.1');
INSERT INTO `operation_log` VALUES (4, 1, '管理员', '采血登记', '登记采血记录，献血者李四，献血量400ml', '2026-07-05 11:57:11', '127.0.0.1');
INSERT INTO `operation_log` VALUES (5, 2, '超级管理员', '检验判定', '判定血液合格，已入库', '2026-07-07 15:02:24', '127.0.0.1');
INSERT INTO `operation_log` VALUES (6, 2, '超级管理员', '导出', '导出2026年7月献血统计报表Excel', '2026-07-07 17:10:00', '127.0.0.1');
INSERT INTO `operation_log` VALUES (7, 3, '张三', '预约', '预约7月10日上午献血活动', '2026-07-04 18:29:19', '127.0.0.1');
INSERT INTO `operation_log` VALUES (8, 2074430000000000001, '刘护士', '出库', '血液出库至淄博市中心医院，O型200ml', '2026-07-05 09:30:00', '127.0.0.1');
INSERT INTO `operation_log` VALUES (2075380529250508801, 2, '超级管理员', '修改活动', '修改献血活动，ID：2072882713903288328，名称：企业职工无偿献血专场', '2026-07-10 08:44:12', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075380604924141570, 2, '超级管理员', '修改活动', '修改献血活动，ID：2072882713903288328，名称：企业职工无偿献血专场', '2026-07-10 08:44:30', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075387808129490945, 2, '超级管理员', '修改活动', '修改献血活动，ID：2072882713903288328，名称：企业职工无偿献血专场', '2026-07-10 09:13:07', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075387852656222210, 2, '超级管理员', '修改活动', '修改献血活动，ID：1，名称：市中心爱心献血屋日常采血', '2026-07-10 09:13:18', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075387975691935746, 2, '超级管理员', '新增采血', '新增采血记录，献血者：王雅婷，血量：400ml', '2026-07-10 09:13:47', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075388209021067265, 2, '超级管理员', '判定检验', '判定血液检验结果，检验ID：2075387975561912322，结果：不合格', '2026-07-10 09:14:43', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075388407457783810, 2, '超级管理员', '判定检验', '判定血液检验结果，检验ID：2075387975561912322，结果：合格', '2026-07-10 09:15:30', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075388464273825794, 2, '超级管理员', '血液入库', '血液入库，采血记录ID：2075387975498997761', '2026-07-10 09:15:43', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075388588811100162, 2, '超级管理员', '血液出库', '血液出库，库存ID：13，用血单位：淄博市中心医院', '2026-07-10 09:16:13', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075402018734043137, 2, '超级管理员', '新增采血', '新增采血记录，献血者：钱六，血量：400ml', '2026-07-10 10:09:35', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2075403630844145665, 2, '超级管理员', '判定检验', '判定血液检验结果，检验ID：2075402018616602625，结果：合格', '2026-07-10 10:15:59', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076601638255935490, 2, '超级管理员', '新增采血', '新增采血记录，献血者：张三，血量：400ml', '2026-07-13 17:36:27', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076616478387093506, 2, '超级管理员', '删除采血', '删除采血记录，ID：2076601637974917122', '2026-07-13 18:35:25', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076616534926311426, 2, '超级管理员', '新增采血', '新增采血记录，献血者：张三，血量：400ml', '2026-07-13 18:35:38', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076620368411189250, 2, '超级管理员', '修改活动', '修改献血活动，ID：1，名称：市中心爱心献血屋日常采血', '2026-07-13 18:50:52', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076620569729392642, 2, '超级管理员', '新增采血', '新增采血记录，献血者：李四，血量：400ml', '2026-07-13 18:51:40', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076620670296219649, 2, '超级管理员', '新增采血', '新增采血记录，献血者：王五，血量：400ml', '2026-07-13 18:52:04', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076923080197554177, 2, '超级管理员', '新增采血', '新增采血记录，献血者：李娜，血量：200ml', '2026-07-14 14:53:44', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076973945927331841, 2, '超级管理员', '新增采血', '新增采血记录，献血者：李一，血量：400ml', '2026-07-14 18:15:52', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076974169940914178, 2, '超级管理员', '新增采血', '新增采血记录，献血者：李二，血量：400ml', '2026-07-14 18:16:45', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076974888261611522, 2, '超级管理员', '血液入库', '血液入库，采血记录ID：2076974169806696450', '2026-07-14 18:19:36', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2076975003407839233, 2, '超级管理员', '血液出库', '血液出库，库存ID：14，用血单位：淄博市市中心医院', '2026-07-14 18:20:04', '0:0:0:0:0:0:0:1');
INSERT INTO `operation_log` VALUES (2077004583942320130, 2, '超级管理员', '新增采血', '新增采血记录，献血者：孙梦琪，血量：400ml', '2026-07-14 20:17:36', '0:0:0:0:0:0:0:1');

-- ----------------------------
-- Table structure for stock_threshold
-- ----------------------------
DROP TABLE IF EXISTS `stock_threshold`;
CREATE TABLE `stock_threshold`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '阈值ID',
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '血型',
  `threshold_value` int NOT NULL DEFAULT 5000 COMMENT '安全库存阈值（ml）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blood_type`(`blood_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '库存阈值配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of stock_threshold
-- ----------------------------
INSERT INTO `stock_threshold` VALUES (1, 'A型', 5000, '2026-07-03 10:36:30', NULL);
INSERT INTO `stock_threshold` VALUES (2, 'B型', 5000, '2026-07-03 10:36:30', NULL);
INSERT INTO `stock_threshold` VALUES (3, 'O型', 5000, '2026-07-03 10:36:30', NULL);
INSERT INTO `stock_threshold` VALUES (4, 'AB型', 5000, '2026-07-03 10:36:30', NULL);

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '消息类型：system-系统消息, collection-采血提醒, test-检验提醒',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `read_status` tinyint NOT NULL DEFAULT 0 COMMENT '阅读状态：0-未读 1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_read_status`(`read_status`) USING BTREE,
  CONSTRAINT `sys_message_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_message
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录用户名',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码（BCrypt加密）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色：ROLE_DONOR/ROLE_ADMIN/ROLE_SUPER_ADMIN/ROLE_TESTER',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2076942780432400387 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户账号表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '管理员', 'ROLE_ADMIN', 0, '2026-07-03 10:35:47', '2026-07-14 18:04:36', 0);
INSERT INTO `sys_user` VALUES (2, 'superadmin', '$2a$10$g75E5/0RPtBhRUhfl5.j3.KqPUs1ohEWHdH7UNFMgYdXjc1amI1Qy', '超级管理员', 'ROLE_SUPER_ADMIN', 0, '2026-07-03 10:35:47', '2026-07-03 11:16:56', 0);
INSERT INTO `sys_user` VALUES (3, 'donor001', '$2a$10$96VA1mfKlA9HykWBqotDwePic2zY/W07BFFfOGxj5FgYzCC06zR9C', '张三', 'ROLE_DONOR', 0, '2026-07-03 10:35:47', '2026-07-12 16:20:54', 0);
INSERT INTO `sys_user` VALUES (2073333544196882434, 'admin01', '$2a$10$w2JBk2W/tjUPz61rOiVazeK7vO9zFedooi/phwStsT.sIHNPdB5zK', 'GGBOND', 'ROLE_DONOR', 0, '2026-07-04 17:10:12', '2026-07-11 17:06:40', 0);
INSERT INTO `sys_user` VALUES (2074430000000000001, 'nurse01', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '刘护士', 'ROLE_ADMIN', 0, '2026-07-01 09:00:00', '2026-07-01 09:00:00', 0);
INSERT INTO `sys_user` VALUES (2074430000000000002, 'donor002', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '陈晓燕', 'ROLE_DONOR', 0, '2026-07-02 10:15:00', '2026-07-02 10:15:00', 0);
INSERT INTO `sys_user` VALUES (2074430000000000003, 'donor003', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '赵建国', 'ROLE_DONOR', 0, '2026-07-02 14:20:00', '2026-07-02 14:20:00', 0);
INSERT INTO `sys_user` VALUES (2074430000000000004, 'donor004', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '孙梦琪', 'ROLE_DONOR', 0, '2026-07-03 08:45:00', '2026-07-03 08:45:00', 0);
INSERT INTO `sys_user` VALUES (2074430000000000005, 'donor005', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '周浩然', 'ROLE_DONOR', 0, '2026-07-03 16:30:00', '2026-07-03 16:30:00', 0);
INSERT INTO `sys_user` VALUES (2074430000000000006, 'donor006', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '吴秀兰', 'ROLE_DONOR', 0, '2026-07-04 11:00:00', '2026-07-04 11:00:00', 0);
INSERT INTO `sys_user` VALUES (2074445831988928513, 'testuser', '$2a$10$3/8S4Xiv2wedk2099WF75O9XQpzCquXMusBTjOgAFc0KAb5nBxU.q', '赵胡克', 'ROLE_ADMIN', 0, '2026-07-07 18:50:02', '2026-07-09 17:14:46', 0);
INSERT INTO `sys_user` VALUES (2075863283633344514, 'superadmin2', '$2a$10$wRey79dPfL5CyPqc7AAODu47f1qjTQ4GdOFQHk9JoHCs5c7y84Gcu', '111', 'ROLE_ADMIN', 0, '2026-07-11 16:42:29', '2026-07-14 16:16:52', 0);
INSERT INTO `sys_user` VALUES (2075863283633344515, 'tester001', '$2a$10$BjIS9y7RP1vdQL/sMuWEQeYKb3tHKwMx7M.KhYVWRLsMzT4OvOija', '检验员张三', 'ROLE_TESTER', 0, '2026-07-13 17:22:20', '2026-07-13 17:22:20', 0);
INSERT INTO `sys_user` VALUES (2076942780432400386, '714admin', '$2a$10$NZhMHa3VPMy4DZ63XrciyeSaeWD9O/6JDIYl96Y9usBI2Qe5wFihC', 'mgy', 'ROLE_ADMIN', 0, '2026-07-14 16:12:01', '2026-07-14 17:25:34', 0);

SET FOREIGN_KEY_CHECKS = 1;
