package com.sdut.blood.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.blood.common.utils.SecurityUtil;
import com.sdut.blood.domain.entity.OperationLog;
import com.sdut.blood.domain.entity.SysUser;
import com.sdut.blood.mapper.OperationLogMapper;
import com.sdut.blood.service.OperationLogService;
import com.sdut.blood.service.SysUserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Resource
    private SysUserService sysUserService;

    @Override
    @Async
    public void saveLog(String operationType, String operationContent) {
        OperationLog log = new OperationLog();
        Long operatorId = SecurityUtil.getCurrentUserId();
        log.setOperatorId(operatorId);
        
        if (operatorId != null) {
            SysUser user = sysUserService.getById(operatorId);
            if (user != null) {
                log.setOperatorName(user.getRealName() != null ? user.getRealName() : user.getUsername());
            }
        }
        
        log.setOperationType(operationType);
        log.setOperationContent(operationContent);
        log.setOperationTime(LocalDateTime.now());
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            log.setIpAddress(ip);
        }
        
        save(log);
    }
}