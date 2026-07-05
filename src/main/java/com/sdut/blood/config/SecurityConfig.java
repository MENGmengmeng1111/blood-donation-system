package com.sdut.blood.config;

import com.sdut.blood.common.result.ResultCode;
import com.sdut.blood.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Spring Security 安全配置类
 * 实现：接口权限控制、JWT认证、密码加密、跨域处理
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启方法级权限注解（@PreAuthorize）
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码加密器：BCrypt强哈希加密，存储用户密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器：登录时校验用户名密码
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 核心安全过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭CSRF（前后端分离项目用JWT，无需CSRF）
                .csrf().disable()
                // 2. 开启跨域支持
                .cors().configurationSource(corsConfigurationSource())
                .and()
                // 3. 关闭Session，采用JWT无状态认证
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 4. 接口权限配置
                .authorizeRequests()
                // 放行：静态资源（HTML、CSS、JS、图片等）
                .antMatchers("/", "/index.html", "/admin.html", "/donor.html", "/*.html", "/*.css", "/*.js", "/css/**", "/js/**", "/static/**", "/images/**").permitAll()
                // 放行：登录、注册、找回密码等公开接口
                .antMatchers("/api/user/login", "/api/user/register", "/api/user/reset-password").permitAll()
                // 放行：Knife4j接口文档所有资源
                .antMatchers("/doc.html", "/webjars/**", "/api-docs", "/swagger-ui.html", "/swagger-ui/**", "/favicon.ico").permitAll()
                // 放行：AI问答公开接口（可选）
                .antMatchers("/api/ai/ask").permitAll()
                // 其余所有接口必须登录认证
                .anyRequest().authenticated()
                .and()
                // 5. 自定义异常处理：未登录、权限不足时返回统一JSON
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"code\":" + ResultCode.UNAUTHORIZED.getCode() + ",\"msg\":\"" + ResultCode.UNAUTHORIZED.getMsg() + "\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"code\":" + ResultCode.PERMISSION_DENIED.getCode() + ",\"msg\":\"" + ResultCode.PERMISSION_DENIED.getMsg() + "\"}");
                });

        // 6. 添加JWT认证过滤器（【后续实现JWT工具类后再取消注释】）
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 跨域配置源：允许前端跨域访问后端接口
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有请求源（生产环境建议指定具体域名）
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 允许携带Cookie/凭证
        config.setAllowCredentials(true);
        // 预检请求有效期3600秒
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
