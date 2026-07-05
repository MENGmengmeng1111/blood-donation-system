package com.sdut.blood.service.impl;

import com.sdut.blood.service.AiKnowledgeService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于本地Markdown的轻量知识库服务
 */
@Service
public class AiKnowledgeServiceImpl implements AiKnowledgeService {

    private static final String DONOR_GUIDE = "献血流程与预约指南";
    private static final String ELIGIBILITY_RULES = "献血资格与间隔规则";
    private static final String TEST_AND_STOCK = "检验与库存规则";
    private static final String SAFETY_NOTICE = "医疗安全边界";

    private final Map<String, String> knowledgeMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        knowledgeMap.put(DONOR_GUIDE, readKnowledge("ai/knowledge/donor_guide.md"));
        knowledgeMap.put(ELIGIBILITY_RULES, readKnowledge("ai/knowledge/eligibility_rules.md"));
        knowledgeMap.put(TEST_AND_STOCK, readKnowledge("ai/knowledge/test_and_stock.md"));
        knowledgeMap.put(SAFETY_NOTICE, readKnowledge("ai/knowledge/safety_notice.md"));
    }

    @Override
    public String searchRelevantKnowledge(String question) {
        StringBuilder builder = new StringBuilder();
        for (String reference : listReferences(question)) {
            String content = knowledgeMap.get(reference);
            if (content != null && !content.trim().isEmpty()) {
                builder.append("## ").append(reference).append('\n')
                        .append(content.trim()).append("\n\n");
            }
        }
        return builder.toString().trim();
    }

    @Override
    public List<String> listReferences(String question) {
        String normalizedQuestion = question == null ? "" : question.toLowerCase();
        List<String> references = new ArrayList<>();

        if (containsAny(normalizedQuestion, "预约", "报名", "活动", "时段", "取消", "流程", "步骤")) {
            references.add(DONOR_GUIDE);
        }
        if (containsAny(normalizedQuestion, "资格", "年龄", "体重", "间隔", "多久", "下次", "状态", "能不能", "可以献血", "暂缓")) {
            references.add(ELIGIBILITY_RULES);
        }
        if (containsAny(normalizedQuestion, "检验", "检查", "初筛", "复检", "合格", "不合格", "入库", "库存", "血液")) {
            references.add(TEST_AND_STOCK);
        }
        if (references.isEmpty()) {
            references.add(DONOR_GUIDE);
            references.add(ELIGIBILITY_RULES);
        }
        references.add(SAFETY_NOTICE);
        return references;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String readKnowledge(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                return "";
            }
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
