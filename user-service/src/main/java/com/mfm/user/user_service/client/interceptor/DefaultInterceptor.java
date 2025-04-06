package com.mfm.user.user_service.client.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import static com.mfm.user.user_service.filter.AppFilter.TRACE_ID;

@Slf4j
public class DefaultInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        log.info("DefaultInterceptor");
        String traceId = MDC.get(TRACE_ID);
        if (traceId != null) {
            template.header(TRACE_ID, traceId);
        }
    }
}