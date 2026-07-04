package com.sdut.blood.service.impl;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiServiceImpl implements AiService {

    @Resource
    private RestTemplate restTemplate;

    @Resource(name = "aiApiKey")
    private String aiApiKey;

    @Resource(name = "aiBaseUrl")
    private String aiBaseUrl;

    @Resource(name = "aiModel")
    private String aiModel;

    private static final String SYSTEM_PROMPT = "你是一个专业的献血管理系统智能助手，负责回答用户关于献血的各种问题。系统基础信息：系统名称为献血管理系统，角色包括普通献血者、管理员、超级管理员。回答范围：1.献血流程：在线预约 -> 现场签到 -> 健康检查 -> 初筛 -> 采血 -> 复检 -> 入库 2.献血条件：年龄18-55周岁，体重男性≥50kg女性≥45kg，全血献血间隔≥6个月，成分血献血间隔≥2周，无传染性疾病无重大病史 3.血液检验：初筛（血红蛋白、血压等）+复检（乙肝、丙肝、艾滋病、梅毒等）4.库存规则：每种血型设置安全阈值，低于阈值自动预警；临期血液优先使用 5.预约方式：选择活动 -> 选择时段（上午/下午）-> 提交预约 -> 查看预约状态。回答要求：语言简洁明了，使用友好亲切的语气，只回答与献血相关的问题，如果问题超出范围，请礼貌地说明无法回答，对于不确定的信息，请说明是参考信息，建议咨询专业医生。请用中文回答。";

    @Override
    public Result<String> ask(String question) {
        if (question == null || question.trim().isEmpty()) {
            return Result.success("请输入您想咨询的问题");
        }

        if (aiApiKey == null || aiApiKey.trim().isEmpty()) {
            return Result.success(getFallbackAnswer(question));
        }

        try {
            String url = aiBaseUrl + "/chat/completions";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiModel);
            
            java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);
            messages.add(systemMessage);
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1024);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                String answer = root.get("choices").get(0).get("message").get("content").asText();
                return Result.success(answer);
            } else {
                return Result.success(getFallbackAnswer(question));
            }
        } catch (Exception e) {
            return Result.success(getFallbackAnswer(question));
        }
    }

    private String getFallbackAnswer(String question) {
        question = question.toLowerCase();
        
        if (question.contains("流程") || question.contains("步骤")) {
            return "献血流程：在线预约 -> 现场签到 -> 健康检查 -> 初筛 -> 采血 -> 复检 -> 入库";
        }
        if (question.contains("年龄") || question.contains("多大")) {
            return "献血年龄要求：18-55周岁";
        }
        if (question.contains("体重") || question.contains("公斤")) {
            return "男性体重要求：≥50kg，女性体重要求：≥45kg";
        }
        if (question.contains("间隔") || question.contains("多久") || question.contains("下次")) {
            return "全血献血间隔：不少于6个月；成分血献血间隔：不少于2周";
        }
        if (question.contains("检验") || question.contains("检查") || question.contains("项目")) {
            return "血液检验项目包括：乙肝、丙肝、艾滋病、梅毒、转氨酶等";
        }
        if (question.contains("预约") || question.contains("报名") || question.contains("时段")) {
            return "预约方式：选择活动 -> 选择时段（上午/下午）-> 提交预约";
        }
        if (question.contains("库存") || question.contains("预警") || question.contains("阈值")) {
            return "库存低于安全阈值时系统会自动预警提醒，临期血液会优先使用";
        }
        if (question.contains("合格") || question.contains("不合格") || question.contains("判定")) {
            return "血液合格判定：初筛合格后采血，复检合格后入库；不合格血液会记录原因";
        }
        if (question.contains("重点关注") || question.contains("标记")) {
            return "多次复检异常的献血者会被系统自动标记为\"重点关注\"";
        }
        
        return "欢迎咨询献血管理系统！常见问题：\n1. 献血年龄要求？\n2. 献血间隔多久？\n3. 如何预约献血？\n4. 血液检验有哪些项目？";
    }
}