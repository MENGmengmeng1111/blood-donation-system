package com.sdut.blood.service;

/**
 * AI用户侧业务上下文服务
 */
public interface AiDonorContextService {

    /**
     * 构建当前用户的献血业务上下文
     */
    String buildCurrentUserContext();

    /**
     * 当前问题是否使用到了个性化上下文
     */
    boolean hasCurrentUserContext();
}
