package com.mfm.user.access_service.config;

import com.mfm.user.access_service.exception.PasswordException;
import com.mfm.user.access_service.exception.UserExistsException;
import com.mfm.user.access_service.handler.ApplicationErrorHandler;
import com.mfm.user.access_service.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final Message message;

    @Bean
    public ApplicationErrorHandler applicationErrorHandler() {
        return new ApplicationErrorHandler(message, List.of(PasswordException.class, UserExistsException.class));
    }
}
