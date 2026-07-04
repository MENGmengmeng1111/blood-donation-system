package com.sdut.blood.controller;

import com.sdut.blood.common.result.Result;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.dto.LoginDTO;
import com.sdut.blood.domain.dto.RegisterDTO;
import com.sdut.blood.domain.entity.SysUser;
import com.sdut.blood.domain.vo.LoginVO;
import com.sdut.blood.service.SysUserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 用户账号控制器
 */
@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 用户登录（公开接口，无需登录权限）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO loginVO = sysUserService.login(dto);
        return Result.success(loginVO);
    }

    /**
     * 用户注册（公开接口，无需登录权限）
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        sysUserService.register(dto);
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<SysUser> getCurrentUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 修改登录密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        return Result.success();
    }
}