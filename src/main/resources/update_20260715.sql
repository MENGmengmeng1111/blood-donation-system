-- ========================================================
-- 增量更新脚本 2026-07-15
-- 新增功能：站内消息、扩展化验项目、血液用途
-- ========================================================

-- 1. 创建消息表（如果不存在）
CREATE TABLE IF NOT EXISTS `sys_message` (
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

-- 2. 给blood_stock表添加血液用途字段（先检查是否存在）
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_stock' AND COLUMN_NAME = 'out_purpose');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_stock` ADD COLUMN `out_purpose` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT \'血液用途\' AFTER `out_unit`', 'SELECT \"out_purpose column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 给blood_stock表的out_unit字段增加长度（从100改为200）
ALTER TABLE `blood_stock` MODIFY COLUMN `out_unit` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用血单位';

-- 4. 给blood_test_indicator表添加新的化验指标字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'ast');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `ast` decimal(5,1) NULL DEFAULT NULL COMMENT \'谷草转氨酶AST(U/L)\' AFTER `alt`', 'SELECT \"ast column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'total_bilirubin');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `total_bilirubin` decimal(5,1) NULL DEFAULT NULL COMMENT \'总胆红素(μmol/L)\' AFTER `ast`', 'SELECT \"total_bilirubin column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'direct_bilirubin');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `direct_bilirubin` decimal(5,1) NULL DEFAULT NULL COMMENT \'直接胆红素(μmol/L)\' AFTER `total_bilirubin`', 'SELECT \"direct_bilirubin column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'hbv_surface_antibody');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `hbv_surface_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT \'乙肝表面抗体：阴性/阳性\' AFTER `hbv_surface_antigen`', 'SELECT \"hbv_surface_antibody column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'hbv_e_antigen');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `hbv_e_antigen` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT \'乙肝e抗原：阴性/阳性\' AFTER `hbv_surface_antibody`', 'SELECT \"hbv_e_antigen column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'hbv_e_antibody');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `hbv_e_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT \'乙肝e抗体：阴性/阳性\' AFTER `hbv_e_antigen`', 'SELECT \"hbv_e_antibody column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'hbv_core_antibody');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `hbv_core_antibody` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT \'乙肝核心抗体：阴性/阳性\' AFTER `hbv_e_antibody`', 'SELECT \"hbv_core_antibody column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'red_blood_cell');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `red_blood_cell` decimal(5,2) NULL DEFAULT NULL COMMENT \'红细胞计数(×10^12/L)\' AFTER `white_blood_cell`', 'SELECT \"red_blood_cell column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'hematocrit');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `hematocrit` decimal(5,1) NULL DEFAULT NULL COMMENT \'红细胞压积(%)\' AFTER `hemoglobin`', 'SELECT \"hematocrit column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'mean_cell_volume');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `mean_cell_volume` decimal(5,1) NULL DEFAULT NULL COMMENT \'平均红细胞体积(fL)\' AFTER `hematocrit`', 'SELECT \"mean_cell_volume column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'mean_cell_hemoglobin');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `mean_cell_hemoglobin` decimal(5,1) NULL DEFAULT NULL COMMENT \'平均红细胞血红蛋白含量(pg)\' AFTER `mean_cell_volume`', 'SELECT \"mean_cell_hemoglobin column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'mean_cell_hemoglobin_concentration');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `mean_cell_hemoglobin_concentration` decimal(5,1) NULL DEFAULT NULL COMMENT \'平均红细胞血红蛋白浓度(g/L)\' AFTER `mean_cell_hemoglobin`', 'SELECT \"mean_cell_hemoglobin_concentration column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'mean_platelet_volume');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `mean_platelet_volume` decimal(5,1) NULL DEFAULT NULL COMMENT \'平均血小板体积(fL)\' AFTER `platelet`', 'SELECT \"mean_platelet_volume column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'glucose');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `glucose` decimal(5,1) NULL DEFAULT NULL COMMENT \'血糖(mmol/L)\' AFTER `mean_platelet_volume`', 'SELECT \"glucose column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'creatinine');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `creatinine` decimal(5,1) NULL DEFAULT NULL COMMENT \'肌酐(μmol/L)\' AFTER `glucose`', 'SELECT \"creatinine column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'blood_urea_nitrogen');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `blood_urea_nitrogen` decimal(5,1) NULL DEFAULT NULL COMMENT \'尿素氮(mmol/L)\' AFTER `creatinine`', 'SELECT \"blood_urea_nitrogen column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'cholesterol');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `cholesterol` decimal(5,1) NULL DEFAULT NULL COMMENT \'总胆固醇(mmol/L)\' AFTER `blood_urea_nitrogen`', 'SELECT \"cholesterol column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'triglyceride');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `triglyceride` decimal(5,1) NULL DEFAULT NULL COMMENT \'甘油三酯(mmol/L)\' AFTER `cholesterol`', 'SELECT \"triglyceride column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'protein');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `protein` decimal(5,1) NULL DEFAULT NULL COMMENT \'总蛋白(g/L)\' AFTER `triglyceride`', 'SELECT \"protein column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'albumin');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `albumin` decimal(5,1) NULL DEFAULT NULL COMMENT \'白蛋白(g/L)\' AFTER `protein`', 'SELECT \"albumin column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'globulin');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `globulin` decimal(5,1) NULL DEFAULT NULL COMMENT \'球蛋白(g/L)\' AFTER `albumin`', 'SELECT \"globulin column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'potassium');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `potassium` decimal(5,2) NULL DEFAULT NULL COMMENT \'钾(mmol/L)\' AFTER `globulin`', 'SELECT \"potassium column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'sodium');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `sodium` decimal(5,1) NULL DEFAULT NULL COMMENT \'钠(mmol/L)\' AFTER `potassium`', 'SELECT \"sodium column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'chloride');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `chloride` decimal(5,1) NULL DEFAULT NULL COMMENT \'氯(mmol/L)\' AFTER `sodium`', 'SELECT \"chloride column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'calcium');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `calcium` decimal(5,2) NULL DEFAULT NULL COMMENT \'钙(mmol/L)\' AFTER `chloride`', 'SELECT \"calcium column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'iron');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `iron` decimal(5,1) NULL DEFAULT NULL COMMENT \'铁(μmol/L)\' AFTER `calcium`', 'SELECT \"iron column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'blood_donation' AND TABLE_NAME = 'blood_test_indicator' AND COLUMN_NAME = 'ferritin');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `blood_test_indicator` ADD COLUMN `ferritin` decimal(7,1) NULL DEFAULT NULL COMMENT \'铁蛋白(ng/mL)\' AFTER `iron`', 'SELECT \"ferritin column already exists\"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. 更新已有出库记录的血液用途（示例数据，根据实际情况调整）
UPDATE `blood_stock` SET `out_purpose` = '临床手术用血' WHERE `out_unit` LIKE '%医院%' AND `out_purpose` IS NULL;
UPDATE `blood_stock` SET `out_purpose` = '治疗性血液置换' WHERE `out_unit` = '渗析' AND `out_purpose` IS NULL;
UPDATE `blood_stock` SET `out_purpose` = '科研教学与质控' WHERE `out_unit` = '过期处理' AND `out_purpose` IS NULL;

-- 更新完成提示
SELECT '增量更新完成' AS result;