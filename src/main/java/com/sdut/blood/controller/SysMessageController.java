package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.SysMessage;
import com.sdut.blood.service.SysMessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class SysMessageController {

    @Resource
    private SysMessageService sysMessageService;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('ROLE_TESTER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<List<SysMessage>> listMessages() {
        Long userId = getCurrentUserId();
        List<SysMessage> messages = sysMessageService.listByUserId(userId);
        return Result.success(messages);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyAuthority('ROLE_TESTER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Map<String, Integer>> getUnreadCount() {
        Long userId = getCurrentUserId();
        int count = sysMessageService.countUnread(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("unreadCount", count);
        return Result.success(result);
    }

    @PutMapping("/read/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_TESTER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Void> markAsRead(@PathVariable Long id) {
        sysMessageService.markAsRead(id);
        return Result.success();
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('ROLE_TESTER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        sysMessageService.markAllAsRead(userId);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_TESTER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        sysMessageService.removeById(id);
        return Result.success();
    }
}