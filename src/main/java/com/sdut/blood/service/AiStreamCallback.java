package com.sdut.blood.service;

/**
 * AI流式响应回调
 */
public interface AiStreamCallback {

    /**
     * 当前流式回答所属会话
     */
    void onSession(Long sessionId, String title);

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
