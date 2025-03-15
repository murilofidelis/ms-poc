package com.mfm.user.user_service.client.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        log.info("--- AccessInterceptor - create access ---");
    }
}