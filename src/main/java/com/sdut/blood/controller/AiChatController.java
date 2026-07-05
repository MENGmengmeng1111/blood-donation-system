package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.AiChatRequest;
import com.sdut.blood.domain.vo.AiChatResponse;
import com.sdut.blood.domain.vo.AiChatSessionVO;
import com.sdut.blood.service.AiChatSessionService;
import com.sdut.blood.service.AiService;
import com.sdut.blood.service.AiStreamCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 智能问答控制器
 */
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Resource
    private AiService aiService;

    @Resource
    private AiChatSessionService aiChatSessionService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 智能咨询问答
     */
    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return aiService.chatForDonor(request.getSessionId(), request.getQuestion(), request.getHistory());
    }

    /**
     * 查询我的AI对话会话列表
     */
    @GetMapping("/session/list")
    public Result<List<AiChatSessionVO>> listSessions() {
        return Result.success(aiChatSessionService.listMySessions());
    }

    /**
     * 查询单个AI对话会话详情
     */
    @GetMapping("/session/{id}")
    public Result<AiChatSessionVO> getSession(@PathVariable Long id) {
        return Result.success(aiChatSessionService.getMySession(id));
    }

    /**
     * 删除AI对话会话
     */
    @DeleteMapping("/session/{id}")
    public Result<Void> deleteSession(@PathVariable Long id) {
        aiChatSessionService.deleteMySession(id);
        return Result.success();
    }

    /**
     * 智能咨询问答 - SSE流式输出
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@Valid @RequestBody AiChatRequest request) {
        SseEmitter emitter = new SseEmitter(120000L);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        CompletableFuture.runAsync(() -> {
            SecurityContextHolder.setContext(securityContext);
            try {
                aiService.streamChatForDonor(
                        request.getSessionId(),
                        request.getQuestion(),
                        request.getHistory(),
                        new AiStreamCallback() {
                            @Override
                            public void onSession(Long sessionId, String title) {
                                sendEvent(emitter, "session", Map.of("sessionId", sessionId, "title", title));
                            }

                            @Override
                            public void onToken(String content) {
                                sendEvent(emitter, "delta", Map.of("content", content));
                            }

                            @Override
                            public void onComplete() {
                                sendEvent(emitter, "done", Map.of("done", true));
                                emitter.complete();
                            }

                            @Override
                            public void onError(String message) {
                                sendEvent(emitter, "error", Map.of("message", message));
                                emitter.complete();
                            }
                        }
                );
            } catch (Exception e) {
                sendEvent(emitter, "error", Map.of("message", e.getMessage() == null ? "AI流式接口调用失败" : e.getMessage()));
                emitter.complete();
            } finally {
                SecurityContextHolder.clearContext();
            }
        });
        return emitter;
    }

    private void sendEvent(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(objectMapper.writeValueAsString(data)));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }
}
