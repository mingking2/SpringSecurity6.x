package com.example.securitytest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    public static final String ALLOWED_METHOD_NAMES = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH,FETCH";

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
//               .allowedOriginPatterns("*")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("fetch","OPTIONS");
//               .allowedHeaders("*")
//               .allowCredentials(true)
//               .maxAge(3600);
    }
}
