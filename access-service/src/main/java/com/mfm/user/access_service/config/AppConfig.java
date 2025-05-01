package com.mfm.user.access_service.config;

import com.mfm.user.access_service.handler.ApplicationErrorHandler;
import com.mfm.user.access_service.util.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ApplicationErrorHandler applicationErrorHandler(final Message message) {
        return new ApplicationErrorHandler(message, new String[]{
                "com.mfm.user.access_service.exception",
                "com.mfm.user.access_service.service.exception"
        });
    }

}
