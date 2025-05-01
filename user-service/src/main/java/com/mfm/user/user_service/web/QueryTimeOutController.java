package com.mfm.user.user_service.web;

import com.mfm.user.user_service.service.QueryTimeOutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RefreshScope
@RestController
@RequestMapping("/time-out")
@RequiredArgsConstructor
public class QueryTimeOutController {

    @Value("${spring.jpa.properties.jakarta.persistence.query.timeout}")
    public String value;

    private final QueryTimeOutService service;

    @GetMapping("/test")
    public ResponseEntity<String> getTime() {
        log.info("getTime");
        String result = service.testTimeOut();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test-custom-time")
    public ResponseEntity<String> getTimeCustom() {
        log.info("getTimeCustom");
        String result = service.testTimeOutCustom();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-value")
    public ResponseEntity<String> getValue() {
        return ResponseEntity.ok(value);
    }
}
