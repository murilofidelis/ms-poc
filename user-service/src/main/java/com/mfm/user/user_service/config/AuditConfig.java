package com.mfm.user.user_service.config;

import com.mfm.user.user_service.security.SecurityUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    @Bean
    public AuthorProvider provideJaversAuthor() {
        return new SimpleAuthorProvider();
    }

    private static class SimpleAuthorProvider implements AuthorProvider {

        @Override
        public String provide() {
            return SecurityUtil.getUserNameAuthenticated();
        }
    }

}
