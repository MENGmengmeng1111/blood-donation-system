package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.SysUser;
import com.sdut.blood.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user-manage")
public class SysUserManageController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(SysUser::getRole, "ROLE_SUPER_ADMIN");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword.trim())
                             .or()
                             .like(SysUser::getRealName, keyword.trim()));
        }
        
        if (role != null && !role.trim().isEmpty()) {
            wrapper.eq(SysUser::getRole, role.trim());
        }
        
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        
        if (sortField != null && !sortField.trim().isEmpty()) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            if ("username".equals(sortField.trim())) {
                if (isAsc) wrapper.orderByAsc(SysUser::getUsername);
                else wrapper.orderByDesc(SysUser::getUsername);
            } else if ("realName".equals(sortField.trim())) {
                if (isAsc) wrapper.orderByAsc(SysUser::getRealName);
                else wrapper.orderByDesc(SysUser::getRealName);
            } else if ("role".equals(sortField.trim())) {
                if (isAsc) wrapper.orderByAsc(SysUser::getRole);
                else wrapper.orderByDesc(SysUser::getRole);
            } else if ("status".equals(sortField.trim())) {
                if (isAsc) wrapper.orderByAsc(SysUser::getStatus);
                else wrapper.orderByDesc(SysUser::getStatus);
            } else if ("createTime".equals(sortField.trim())) {
                if (isAsc) wrapper.orderByAsc(SysUser::getCreateTime);
                else wrapper.orderByDesc(SysUser::getCreateTime);
            } else {
                wrapper.orderByDesc(SysUser::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(SysUser::getCreateTime);
        }
        
        sysUserService.page(page, wrapper);
        page.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<SysUser> getById(@PathVariable String id) {
        SysUser user = sysUserService.getById(Long.parseLong(id));
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> create(@RequestBody SysUser user) {
        if (sysUserService.getByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.save(user);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> update(@PathVariable String id, @RequestBody SysUser user) {
        Long userId = Long.parseLong(id);
        SysUser existing = sysUserService.getById(userId);
        if (existing == null) {
            return Result.error("用户不存在");
        }
        if ("ROLE_SUPER_ADMIN".equals(existing.getRole())) {
            return Result.error("不能修改超级管理员");
        }
        if (!existing.getUsername().equals(user.getUsername()) && sysUserService.getByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        user.setId(userId);
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable String id) {
        Long userId = Long.parseLong(id);
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if ("ROLE_SUPER_ADMIN".equals(user.getRole())) {
            return Result.error("不能删除超级管理员");
        }
        sysUserService.removeById(userId);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> updateStatus(@PathVariable String id, @RequestParam Integer status) {
        Long userId = Long.parseLong(id);
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if ("ROLE_SUPER_ADMIN".equals(user.getRole())) {
            return Result.error("不能修改超级管理员状态");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(user);
        return Result.success();
    }
}