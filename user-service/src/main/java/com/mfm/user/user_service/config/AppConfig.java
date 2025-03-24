package com.mfm.user.user_service.config;

import com.mfm.user.user_service.handler.ApplicationErrorHandler;
import com.mfm.user.user_service.util.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ApplicationErrorHandler applicationErrorHandler(final Message message) {
        return new ApplicationErrorHandler(message, new String[]{
                "com.mfm.user.user_service.exception"
        });
    }

}
