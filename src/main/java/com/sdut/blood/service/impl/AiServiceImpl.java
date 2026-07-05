package com.sdut.blood.service.impl;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.vo.AiChatResponse;
import com.sdut.blood.service.AiDonorContextService;
import com.sdut.blood.service.AiKnowledgeService;
import com.sdut.blood.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
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

    private static final String SYSTEM_PROMPT = """
            你是献血管理系统的用户侧智能咨询助手。
            你会收到两类内部参考材料：
            1. 【本地知识库】：相当于系统内置 skill，用于提供献血流程、预约规则、资格间隔、检验与安全边界等规则。
            2. 【当前用户系统记录】：从数据库读取的当前登录用户档案、预约记录和献血记录。

            回答时必须遵守：
            - 只回答献血管理系统、献血流程、预约、献血资格、检验结果和献血记录相关问题。
            - 优先结合当前用户系统记录；当用户问“我现在还能献血吗”“我什么时候能献血”等个性化问题时，必须基于记录做初步判断。
            - 本地知识库和系统记录是内部参考，不要机械复述“我已读取你的系统档案”，也不要每次都列出“参考来源”。
            - 如果系统记录缺失，请明确告诉用户需要先完善个人档案或等待系统产生记录。
            - 不要编造系统记录中没有的信息；不确定时说明缺少哪些信息。
            - 不做医疗诊断，不替代医生或血站工作人员判断。
            - 只有在涉及健康状况、疾病、用药、检验异常、能否献血等判断时，才提醒“最终以现场医护或血站工作人员判断为准”。
            - 使用简洁、自然、友好的中文回答。
            """;

    private static final String AI_NOT_CONFIGURED_MESSAGE =
            "AI模型暂未配置，当前无法调用外部大模型。请在 config/application-ai-secret.yml 中填写 ai.openai.api-key、base-url 和 model 后重启项目。";

    private static final String AI_CALL_FAILED_MESSAGE =
            "AI模型调用失败，当前无法生成回答。请检查网络、API Key、base-url/model 配置或模型服务可用性后重试。";

    @Override
    public Result<String> ask(String question) {
        if (question == null || question.trim().isEmpty()) {
            return Result.success("请输入您想咨询的问题");
        }

        if (!isAiConfigured()) {
            return Result.error(AI_NOT_CONFIGURED_MESSAGE);
        }

        String knowledge = aiKnowledgeService.searchRelevantKnowledge(question);
        String answer = callAi(question, knowledge, "未读取登录用户上下文。");
        if (answer == null) {
            return Result.error(AI_CALL_FAILED_MESSAGE);
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

        if (!isAiConfigured()) {
            response.setAnswer(AI_NOT_CONFIGURED_MESSAGE);
            response.setReferences(List.of());
            response.setPersonalized(aiDonorContextService.hasCurrentUserContext());
            return Result.error(AI_NOT_CONFIGURED_MESSAGE);
        }

        String answer = callAi(cleanQuestion, knowledge, context);
        if (answer == null) {
            response.setAnswer(AI_CALL_FAILED_MESSAGE);
            response.setReferences(List.of());
            response.setPersonalized(aiDonorContextService.hasCurrentUserContext());
            return Result.error(AI_CALL_FAILED_MESSAGE);
        }

        response.setAnswer(answer);
        response.setReferences(references);
        response.setPersonalized(aiDonorContextService.hasCurrentUserContext());
        return Result.success(response);
    }

    private String callAi(String question, String knowledge, String context) {
        if (!isAiConfigured()) {
            return null;
        }

        try {
            String url = buildChatCompletionsUrl();
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiModel);
            
            List<Map<String, String>> messages = new ArrayList<>();
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
            log.warn("AI chat completion call failed: {}", e.getMessage());
            return null;
        }
        return null;
    }

    private String buildSystemPrompt(String knowledge, String context) {
        return SYSTEM_PROMPT
                + "\n\n【本地知识库】\n" + (knowledge == null || knowledge.isEmpty() ? "暂无命中知识。" : knowledge)
                + "\n\n【当前用户系统记录】\n" + (context == null || context.isEmpty() ? "暂无用户上下文。" : context)
                + "\n\n请直接回答用户问题。必要时可以说“根据系统记录”或“参考系统规则”，但不要在回答末尾固定追加参考列表。";
    }

    private boolean isAiConfigured() {
        return StringUtils.hasText(aiApiKey)
                && StringUtils.hasText(aiBaseUrl)
                && StringUtils.hasText(aiModel);
    }

    private String buildChatCompletionsUrl() {
        String normalizedBaseUrl = aiBaseUrl.trim();
        while (normalizedBaseUrl.endsWith("/")) {
            normalizedBaseUrl = normalizedBaseUrl.substring(0, normalizedBaseUrl.length() - 1);
        }
        if (normalizedBaseUrl.endsWith("/chat/completions")) {
            return normalizedBaseUrl;
        }
        return normalizedBaseUrl + "/chat/completions";
    }

}
