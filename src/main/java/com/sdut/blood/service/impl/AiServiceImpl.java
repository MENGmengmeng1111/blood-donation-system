package com.sdut.blood.service.impl;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.vo.AiChatResponse;
import com.sdut.blood.service.AiDonorContextService;
import com.sdut.blood.service.AiKnowledgeService;
import com.sdut.blood.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
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

    @Resource
    private AiKnowledgeService aiKnowledgeService;

    @Resource
    private AiDonorContextService aiDonorContextService;

    private static final String SYSTEM_PROMPT = "你是献血管理系统的用户侧智能咨询助手。你的职责是结合知识库和系统记录，回答献血者关于预约、献血资格、献血流程、检验结果和献血记录的问题。回答必须遵守：1. 只回答献血管理系统和献血常识相关问题；2. 不做医疗诊断，不替代医生或血站工作人员判断；3. 涉及能否献血、疾病、用药、检验异常时，必须提醒最终以现场医护或血站判断为准；4. 不要编造系统记录中没有的信息；5. 语言简洁、友好、中文回答。";

    @Override
    public Result<String> ask(String question) {
        if (question == null || question.trim().isEmpty()) {
            return Result.success("请输入您想咨询的问题");
        }

        String knowledge = aiKnowledgeService.searchRelevantKnowledge(question);
        String answer = callAi(question, knowledge, "未读取登录用户上下文。");
        if (answer == null) {
            answer = getFallbackAnswer(question);
        }
        return Result.success(answer);
    }

    @Override
    public Result<AiChatResponse> chatForDonor(String question) {
        AiChatResponse response = new AiChatResponse();
        if (question == null || question.trim().isEmpty()) {
            response.setAnswer("请输入您想咨询的问题");
            response.setReferences(List.of());
            response.setPersonalized(false);
            return Result.success(response);
        }

        String cleanQuestion = question.trim();
        String knowledge = aiKnowledgeService.searchRelevantKnowledge(cleanQuestion);
        String context = aiDonorContextService.buildCurrentUserContext();
        List<String> references = aiKnowledgeService.listReferences(cleanQuestion);

        String answer = callAi(cleanQuestion, knowledge, context);
        if (answer == null) {
            answer = getFallbackAnswer(cleanQuestion);
            if (aiDonorContextService.hasCurrentUserContext()) {
                answer += "\n\n我已读取你的系统档案、预约和献血记录作为参考；涉及具体能否献血，请以现场医护或血站工作人员判断为准。";
            }
        }

        response.setAnswer(answer);
        response.setReferences(references);
        response.setPersonalized(aiDonorContextService.hasCurrentUserContext());
        return Result.success(response);
    }

    private String callAi(String question, String knowledge, String context) {
        if (aiApiKey == null || aiApiKey.trim().isEmpty()) {
            return null;
        }

        try {
            String url = aiBaseUrl + "/chat/completions";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiModel);
            
            java.util.List<Map<String, String>> messages = new java.util.ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildSystemPrompt(knowledge, context));
            messages.add(systemMessage);
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", question.trim());
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 1024);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode choices = root.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode content = choices.get(0).get("message").get("content");
                    return content == null ? null : content.asText();
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String buildSystemPrompt(String knowledge, String context) {
        return SYSTEM_PROMPT
                + "\n\n【本地知识库】\n" + (knowledge == null || knowledge.isEmpty() ? "暂无命中知识。" : knowledge)
                + "\n\n【当前用户系统记录】\n" + (context == null || context.isEmpty() ? "暂无用户上下文。" : context)
                + "\n\n回答要求：先直接回答用户问题；如引用系统记录，请说明“根据系统记录”；如引用知识库规则，请说明是参考规则；结尾在涉及健康判断时补充“最终以现场医护或血站判断为准”。";
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
