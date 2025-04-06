package com.mfm.user.user_service.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
public class Message {

    private MessageSourceAccessor accessor;
    private final MessageSource messageSource;

    @Autowired
    public Message(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @PostConstruct
    public void initComponent() {
        accessor = new MessageSourceAccessor(messageSource, new Locale("pt_br"));
    }

    public String get(String msg) {
        try {
            return accessor.getMessage(msg);
        } catch (RuntimeException ex) {
            log.warn("message not found!");
            return null;
        }
    }
}
