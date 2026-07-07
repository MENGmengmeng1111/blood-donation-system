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
    public Result<List<SysUser>> list() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(SysUser::getRole, "ROLE_SUPER_ADMIN");
        wrapper.orderByDesc(SysUser::getCreateTime);
        List<SysUser> list = sysUserService.list(wrapper);
        list.forEach(user -> user.setPassword(null));
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
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
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        SysUser existing = sysUserService.getById(id);
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
        user.setId(id);
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if ("ROLE_SUPER_ADMIN".equals(user.getRole())) {
            return Result.error("不能删除超级管理员");
        }
        sysUserService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = sysUserService.getById(id);
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