package com.sdut.blood.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.blood.common.constants.UserConstants;
import com.sdut.blood.common.result.Result;
import com.sdut.blood.domain.entity.SysUser;
import com.sdut.blood.service.SysUserService;
import com.sdut.blood.service.StockThresholdService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 超级管理员控制器
 */
@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
public class SuperAdminController {

    @Resource
    private StockThresholdService stockThresholdService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 设置各血型库存安全阈值
     */
    @PostMapping("/threshold/set")
    public Result<Void> setStockThreshold(@RequestParam String bloodType, @RequestParam Integer thresholdValue) {
        stockThresholdService.setThreshold(bloodType, thresholdValue);
        return Result.success();
    }

    /**
     * 分页查询管理员账号列表
     */
    @GetMapping("/admin/list")
    public Result<Page<SysUser>> listAdmins(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUser::getRole, UserConstants.ROLE_ADMIN, UserConstants.ROLE_SUPER_ADMIN)
                .orderByDesc(SysUser::getCreateTime);
        sysUserService.page(page, wrapper);
        // 密码脱敏
        page.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(page);
    }

    /**
     * 新增管理员账号（默认密码123456）
     */
    @PostMapping("/admin/add")
    public Result<Void> addAdmin(@RequestBody SysUser user) {
        user.setPassword(passwordEncoder.encode(UserConstants.DEFAULT_PASSWORD));
        user.setStatus(0);
        sysUserService.save(user);
        return Result.success();
    }

    /**
     * 修改管理员账号信息
     */
    @PutMapping("/admin/update")
    public Result<Void> updateAdmin(@RequestBody SysUser user) {
        // 禁止通过该接口修改密码
        user.setPassword(null);
        sysUserService.updateById(user);
        return Result.success();
    }

    /**
     * 删除管理员账号（逻辑删除）
     */
    @DeleteMapping("/admin/{id}")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success();
    }
}