package com.sdut.blood.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.blood.domain.dto.LoginDTO;
import com.sdut.blood.domain.dto.RegisterDTO;
import com.sdut.blood.domain.entity.SysUser;
import com.sdut.blood.domain.vo.LoginVO;

/**
 * 用户账号服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);
}