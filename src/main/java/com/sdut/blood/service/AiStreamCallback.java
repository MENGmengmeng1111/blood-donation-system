package com.sdut.blood.service;

/**
 * AI流式响应回调
 */
public interface AiStreamCallback {

    /**
     * 收到一段模型增量内容
     */
    void onToken(String content);

    /**
     * 流式响应正常结束
     */
    void onComplete();

    /**
     * 流式响应异常
     */
    void onError(String message);
}
