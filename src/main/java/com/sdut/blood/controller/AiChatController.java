package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.AiChatRequest;
import com.sdut.blood.domain.vo.AiChatResponse;
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
    private ObjectMapper objectMapper;

    /**
     * 智能咨询问答
     */
    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return aiService.chatForDonor(request.getQuestion(), request.getHistory());
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
                        request.getQuestion(),
                        request.getHistory(),
                        new AiStreamCallback() {
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
