package com.example.quan_ly_don_xin_nghi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Cho phép frontend truy cập
        config.addAllowedOrigin("http://localhost:3000");
        
        // Cho phép các phương thức HTTP
        config.addAllowedMethod("*");
        
        // Cho phép tất cả headers
        config.addAllowedHeader("*");
        
        // Cho phép credentials
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 