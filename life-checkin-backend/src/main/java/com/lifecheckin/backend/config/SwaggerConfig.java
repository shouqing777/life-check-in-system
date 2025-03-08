package com.lifecheckin.backend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("spring")
                .pathsToMatch("/api/**") // 確保這裡的路徑與您的 API 路徑匹配
                .packagesToScan("com.lifecheckin.backend.controller") // 自動掃描控制器
                .build();
    }
}