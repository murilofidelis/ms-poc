package com.mfm.user.access_service.exception;

public class BusinessException extends RuntimeException {

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }


    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
