package com.mfm.user.access_service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandling extends ResponseEntityExceptionHandler {

    private final ApplicationErrorHandler errorHandler;

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exceptionHandler(Exception exception, NativeWebRequest request) {

        ApiError apiError = errorHandler.buildError(exception, request);

        return handleExceptionInternal(exception, apiError, new HttpHeaders(), HttpStatusCode.valueOf(apiError.getStatus()), request);
    }
}
