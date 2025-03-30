package com.mfm.user.user_service.client.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
public class AccessInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        log.info("--- AccessInterceptor ---");
        final var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String token = jwtAuth.getToken().getTokenValue();
            template.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(token));
        }
    }
}