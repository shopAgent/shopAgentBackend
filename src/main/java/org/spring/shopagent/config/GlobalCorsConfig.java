package org.spring.shopagent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // 모든 경로에 대해
                        .allowedOrigins("*")  // 모든 출처 허용
                        .allowedMethods("*")  // 모든 HTTP 메서드 허용 (GET, POST, PUT 등)
                        .allowedHeaders("*")  // 모든 헤더 허용
                        .allowCredentials(false);  // 인증 정보는 비허용 (true 설정 시 allowedOrigins에 * 사용 불가)
            }
        };
    }
}