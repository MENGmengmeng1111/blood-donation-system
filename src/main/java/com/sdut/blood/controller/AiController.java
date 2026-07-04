package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.service.AiService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * AI智能问答控制器
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Resource
    private AiService aiService;

    /**
     * 智能咨询问答
     */
    @GetMapping("/ask")
    public Result<String> ask(@RequestParam String question) {
        return aiService.ask(question);
    }
}