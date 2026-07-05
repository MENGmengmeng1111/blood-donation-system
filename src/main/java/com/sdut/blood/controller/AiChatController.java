package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.dto.AiChatRequest;
import com.sdut.blood.domain.vo.AiChatResponse;
import com.sdut.blood.service.AiService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 智能问答控制器
 */
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Resource
    private AiService aiService;

    /**
     * 智能咨询问答
     */
    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return aiService.chatForDonor(request.getQuestion());
    }
}
