package com.mfm.user.user_service.client.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class AccessInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        log.info("--- AccessInterceptor ---");

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        String authToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        template.header(HttpHeaders.AUTHORIZATION, authToken);

    }
}