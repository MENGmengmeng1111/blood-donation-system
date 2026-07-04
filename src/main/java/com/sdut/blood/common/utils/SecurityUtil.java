package com.sdut.blood.common.utils;

import com.sdut.blood.common.exception.BusinessException;
import com.sdut.blood.common.result.ResultCode;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * 安全上下文工具类
 * 获取当前登录用户ID、角色
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        if (principal instanceof Number) {
            return ((Number) principal).longValue();
        }
        if (principal instanceof String) {
            try {
                return Long.valueOf((String) principal);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户ID，未登录或登录态异常时抛出未授权异常
     */
    public static Long requireCurrentUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    /**
     * 获取当前登录用户角色
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return null;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return null;
        }
        return authorities.iterator().next().getAuthority();
    }

    private static boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
