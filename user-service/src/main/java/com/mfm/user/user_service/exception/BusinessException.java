package com.mfm.user.user_service.exception;

public class BusinessException extends RuntimeException {

    private final MessageKey key;

    public BusinessException(MessageKey key) {
        super();
        this.key = key;
    }

    public MessageKey getKey() {
        return key;
    }
}
