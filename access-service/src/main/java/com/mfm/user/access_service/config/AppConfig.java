package com.mfm.user.access_service.config;

import com.mfm.user.access_service.handler.ApplicationErrorHandler;
import com.mfm.user.access_service.util.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ApplicationErrorHandler applicationErrorHandler(final Message message) {
        return new ApplicationErrorHandler(message, new String[]{
                "com.mfm.user.access_service.exception",
                "com.mfm.user.access_service.service.exception"
        });
    }
}
