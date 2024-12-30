package com.example.test_redis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置全局 CORS 設置，允許所有請求來源
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")  // 允許來自 http://localhost:8080 的請求
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 允許的 HTTP 方法
                .allowedHeaders("*") // 允許所有的標頭
                .exposedHeaders("friends-count"); // 允許前端取得的標頭);
    }
}
