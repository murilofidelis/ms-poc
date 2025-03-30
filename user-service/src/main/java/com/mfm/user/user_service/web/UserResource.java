package com.mfm.user.user_service.web;

import com.mfm.user.user_service.domain.dto.DUser;
import com.mfm.user.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;

    @PostMapping
    @Secured("ROLE_MS_WRITE")
    public ResponseEntity<DUser> create(@RequestBody @Valid DUser user) {
        log.info("create user ...");
        DUser userSave = userService.create(user);
        log.info("create user end");
        return ResponseEntity.ok(userSave);
    }
}
