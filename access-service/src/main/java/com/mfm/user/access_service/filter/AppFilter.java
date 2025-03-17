package com.mfm.user.access_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class AppFilter {

    public static final String TRACE_ID = "X-Trace-Id";

    @Bean
    public FilterRegistrationBean<AppFilterIntercept> filter() {
        FilterRegistrationBean<AppFilterIntercept> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AppFilterIntercept());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    static class AppFilterIntercept extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            try {
                String traceId = request.getHeader(TRACE_ID);
                if (traceId == null) {
                    traceId = UUID.randomUUID().toString();
                }
                MDC.put(TRACE_ID, traceId);
                filterChain.doFilter(request, response);
            } finally {
                MDC.clear();
            }
        }
    }

}




