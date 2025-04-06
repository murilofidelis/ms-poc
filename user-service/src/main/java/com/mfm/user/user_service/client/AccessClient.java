package com.mfm.user.user_service.client;

import com.mfm.user.user_service.client.dto.DAccess;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "access-client",
        url = "${spring.cloud.openfeign.client.config.access-client.url}")
public interface AccessClient {

    @PostMapping(value = "/access", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    DAccess createAccess(@RequestBody DAccess access);
}
