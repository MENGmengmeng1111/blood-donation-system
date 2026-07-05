package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.utils.JwtUtil;
import com.sdut.blood.domain.dto.LoginDTO;
import com.sdut.blood.domain.dto.RegisterDTO;
import com.sdut.blood.domain.entity.SysUser;
import com.sdut.blood.domain.vo.LoginVO;
import com.sdut.blood.mapper.SysUserMapper;
import com.sdut.blood.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO dto) {
        log.info("登录请求开始，username: {}", dto.getUsername());
        
        SysUser user = getByUsername(dto.getUsername());
        log.info("查询到的用户: {}", user != null ? "存在(id=" + user.getId() + ",username=" + user.getUsername() + ")" : "不存在");
        
        if (user == null) {
            log.warn("登录失败: 用户不存在");
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 1) {
            log.warn("登录失败: 账号已被禁用");
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        String storedPassword = user.getPassword();
        boolean isBcryptFormat = storedPassword != null && (
            storedPassword.startsWith("$2a$") || 
            storedPassword.startsWith("$2b$") || 
            storedPassword.startsWith("$2y$")
        );
        
        boolean passwordMatch = false;
        
        if (isBcryptFormat) {
            passwordMatch = passwordEncoder.matches(dto.getPassword(), storedPassword);
        }
        
        if (!passwordMatch) {
            if (dto.getPassword().equals(storedPassword)) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
                updateById(user);
                log.info("已完成历史密码格式升级，userId: {}", user.getId());
                passwordMatch = true;
            } else {
                try {
                    passwordMatch = passwordEncoder.matches(dto.getPassword(), storedPassword);
                } catch (Exception e) {
                    log.warn("BCrypt校验异常: {}", e.getMessage());
                }
            }
        }

        if (!passwordMatch) {
            log.warn("登录失败: 密码校验不通过");
            throw new BusinessException("用户名或密码错误");
        }

        log.info("登录成功，生成Token...");
        String token = JwtUtil.generateToken(user.getId(), user.getRole());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        log.info("登录成功完成，用户角色: {}", user.getRole());
        return vo;
    }

    @Override
    public void register(RegisterDTO dto) {
        SysUser existUser = getByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在，请选择其他用户名");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setRole("ROLE_DONOR");
        user.setStatus(0);
        save(user);
    }

    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
}
