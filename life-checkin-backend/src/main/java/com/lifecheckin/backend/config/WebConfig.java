package com.lifecheckin.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置類
 * 設置跨域資源共享(CORS)等Web相關配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域資源共享
     * @param registry CORS註冊表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") // 前端開發伺服器
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 1小時
    }
}