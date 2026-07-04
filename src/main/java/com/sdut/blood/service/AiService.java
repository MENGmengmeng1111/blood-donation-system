package com.sdut.blood.service;

import com.sdut.blood.common.result.Result;

/**
 * AI智能问答服务接口
 */
public interface AiService {

    /**
     * 智能咨询问答
     */
    Result<String> ask(String question);
}