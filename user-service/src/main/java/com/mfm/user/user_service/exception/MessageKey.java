package com.mfm.user.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MessageKey {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "MSG_01");

    private final HttpStatus status;
    private final String keyMessage;


}
