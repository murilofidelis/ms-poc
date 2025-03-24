package com.mfm.user.access_service.web;

import com.mfm.user.access_service.domain.dto.DAccess;
import com.mfm.user.access_service.service.AccessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
public class AccessResource {

    private final AccessService accessService;

    @PostMapping
    public ResponseEntity<DAccess> create(@RequestBody @Valid DAccess access) {
        log.info("create access ...");
        DAccess userSaveSave = accessService.create(access);
        return ResponseEntity.ok(userSaveSave);
    }
}
