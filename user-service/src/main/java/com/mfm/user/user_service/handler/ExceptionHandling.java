package com.mfm.user.user_service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandling extends ResponseEntityExceptionHandler {

    private final ApplicationErrorHandler errorHandler;

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleExceptionCustom(Exception exception, WebRequest request) {
        ApiError apiError = errorHandler.buildError(exception, request);
        return handleExceptionInternal(exception, apiError, new HttpHeaders(), HttpStatusCode.valueOf(apiError.getStatus()), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = errorHandler.buildError(exception, request);
        return handleExceptionInternal(exception, apiError, new HttpHeaders(), HttpStatusCode.valueOf(apiError.getStatus()), request);
    }

}
