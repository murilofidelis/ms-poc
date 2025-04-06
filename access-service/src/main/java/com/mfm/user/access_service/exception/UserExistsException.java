package com.mfm.user.access_service.exception;

import com.mfm.user.access_service.handler.AppException;

@AppException(msgCod = "MSG_01", httpStatusCod = 400)
public class UserExistsException extends RuntimeException {

    public UserExistsException() {
    }

    public UserExistsException(String message) {
        super(message);
    }

}
