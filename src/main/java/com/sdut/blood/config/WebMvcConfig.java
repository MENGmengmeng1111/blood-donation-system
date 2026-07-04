package com.sdut.blood.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigInteger;

/**
 * Web MVC 全局配置
 * 功能：全局日期格式化、静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 全局Jackson日期格式化配置
     * 统一所有接口的日期入参、出参格式，避免前后端格式不兼容
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册Java8时间模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 序列化（后端返回前端）
        javaTimeModule.addSerializer(LocalDateTime.class, 
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class, 
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        
        // 反序列化（前端传入后端）
        javaTimeModule.addDeserializer(LocalDateTime.class, 
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, 
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        
        objectMapper.registerModule(javaTimeModule);
        
        // 将Long类型序列化为String，避免JavaScript精度丢失（雪花算法ID超过2^53-1）
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, new com.fasterxml.jackson.databind.ser.std.ToStringSerializer(Long.class));
        simpleModule.addSerializer(Long.TYPE, new com.fasterxml.jackson.databind.ser.std.ToStringSerializer(Long.TYPE));
        objectMapper.registerModule(simpleModule);
        
        // 关闭日期转时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略未知属性，避免前端传多余字段报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return objectMapper;
    }

    /**
     * 静态资源映射：放行Knife4j文档静态资源和自定义静态页面
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}