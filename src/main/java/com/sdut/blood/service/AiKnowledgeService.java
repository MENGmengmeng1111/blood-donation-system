package com.sdut.blood.service;

import java.util.List;

/**
 * AI本地知识库服务
 */
public interface AiKnowledgeService {

    /**
     * 根据问题检索相关知识内容
     */
    String searchRelevantKnowledge(String question);

    /**
     * 获取本次命中的知识来源
     */
    List<String> listReferences(String question);
}
