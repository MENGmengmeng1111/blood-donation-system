package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.OperationLog;
import com.sdut.blood.service.OperationLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/log")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<List<OperationLog>> list() {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperationLog::getOperationTime);
        List<OperationLog> list = operationLogService.list(wrapper);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        operationLogService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        operationLogService.removeByIds(ids);
        return Result.success();
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> clearAll() {
        operationLogService.remove(new LambdaQueryWrapper<>());
        return Result.success();
    }
}