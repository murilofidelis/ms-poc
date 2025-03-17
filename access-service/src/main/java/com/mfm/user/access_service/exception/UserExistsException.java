package com.mfm.user.access_service.exception;

import com.mfm.user.access_service.handler.AppException;
import org.springframework.http.HttpStatus;

@AppException(msgCod = "MSG_01", httpStatus = HttpStatus.BAD_REQUEST)
public class UserExistsException extends RuntimeException {

    public UserExistsException() {
    }

    public UserExistsException(String message) {
        super(message);
    }

}
