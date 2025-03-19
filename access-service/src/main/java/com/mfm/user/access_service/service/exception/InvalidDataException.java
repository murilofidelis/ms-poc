package com.mfm.user.access_service.service.exception;

import com.mfm.user.access_service.handler.AppException;

@AppException(msgCod = "MSG_02", httpStatusCod = 400)
public class InvalidDataException extends  RuntimeException{

    public InvalidDataException(String message) {
        super(message);
    }
}
