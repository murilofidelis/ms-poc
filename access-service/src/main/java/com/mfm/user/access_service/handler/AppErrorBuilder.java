package com.mfm.user.access_service.handler;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mfm.user.access_service.exception.PasswordException;
import com.mfm.user.access_service.exception.UserExistsException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class AppErrorBuilder {

    private static final String REQUEST_PARAMS = "params";

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private Integer status = httpStatus.value();
    private String title;
    private String detail;
    private String methodHttp;
    private String path;
    private String dataUser;
    private String className;
    private String methodName;
    private int lineError;
    private String exceptionName;
    private Map<String, Object> extrasParams;
    private String cause;
    private String stackTraceCause;
    private String stack;
    private Integer numRowsStacktrace;

    private NativeWebRequest request;

    private Exception exception;


    private final List<Integer> warning = List.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.NOT_FOUND.value());


    private final List<Class<? extends Exception>> businessExceptions = List.of(
            UserExistsException.class,
            PasswordException.class);

    private AppErrorBuilder() {
    }

    protected static AppErrorBuilder builder(Exception exception, NativeWebRequest request) {
        log.info("Create AppErrorBuilder");
        return new AppErrorBuilder(exception, request);
    }

    private AppErrorBuilder(Exception exception, NativeWebRequest request) {
        this.exception = exception;
        this.request = request;
    }

    protected AppErrorBuilder addStatus() {
        if (this.exception != null) {
            ResponseStatus responseStatus = this.exception.getClass().getAnnotation(ResponseStatus.class);
            if (responseStatus != null) {
                this.httpStatus = responseStatus.value();
                this.status = httpStatus.value();
            }
        }
        return this;
    }

    protected AppErrorBuilder addTitle() {
        if (this.exception != null) {
            this.title = this.httpStatus.getReasonPhrase();
        }
        return this;
    }

    protected AppErrorBuilder adduser() {
        if (request.getUserPrincipal() != null) {
            this.dataUser = request.getUserPrincipal().getName();
        }
        return this;
    }

    protected AppErrorBuilder addMethodType() {
        if (this.request != null) {
            this.methodHttp = ((ServletWebRequest) this.request).getRequest().getMethod();
        }
        return this;
    }

    protected AppErrorBuilder addDetail() {
        if (this.exception != null) {
            this.detail = this.exception.getMessage();
        }
        return this;
    }

    protected AppErrorBuilder addPath() {
        if (this.request != null) {
            this.path = ((ServletWebRequest) this.request).getRequest().getRequestURI();
        }
        return this;
    }

    protected AppErrorBuilder addCause() {
        if (this.exception != null && this.exception.getCause() != null) {
            this.cause = this.exception.getCause().getClass().getName();
            this.stackTraceCause = this.getStackTrace(this.exception.getCause().getStackTrace(), 3);
        }
        return this;
    }

    protected AppErrorBuilder addQuerySearchParams() {
        if (this.request != null) {
            Map<String, String> map = new LinkedHashMap<>();
            Map<String, String[]> requestParams = ((ServletWebRequest) request).getRequest().getParameterMap();
            for (Map.Entry<String, String[]> params : requestParams.entrySet()) {
                String[] valueParam = params.getValue();
                String value = null;
                if (valueParam != null && valueParam.length == 1) {
                    value = valueParam[0];
                } else if (valueParam != null && valueParam.length > 1) {
                    value = Arrays.toString(valueParam);
                }
                map.put(params.getKey(), value);
            }
            if (extrasParams == null) {
                this.extrasParams = new HashMap<>();
            }
            this.extrasParams.put(REQUEST_PARAMS, map);
        }
        return this;
    }

    protected AppErrorBuilder addInfoClassError() {
        StackTraceElement[] stackTrace = null;
        if (this.exception != null) {
            stackTrace = this.exception.getStackTrace();
            this.exceptionName = this.exception.getClass().getName();
        }
        if (stackTrace != null && stackTrace.length > 0) {
            StackTraceElement stackTraceElement = stackTrace[0];
            this.className = stackTraceElement.getClassName();
            this.methodName = stackTraceElement.getMethodName();
            this.lineError = stackTraceElement.getLineNumber();
        }
        return this;
    }

    protected AppErrorBuilder addStackTrace(int numRows) {
        this.numRowsStacktrace = numRows;
        this.stackTrace(numRows);
        return this;
    }

    protected AppErrorBuilder addStackTrace() {
        this.stackTrace(null);
        return this;
    }

    protected AppErrorBuilder build() {
        this.showError();
        return this;
    }

    private boolean isBusinessException() {
        boolean isStatusBusiness = (this.status != null && this.warning.contains(this.status));
        boolean isBusinessException = (this.exception != null && this.businessExceptions.contains(this.exception.getClass()));
        return (isStatusBusiness || isBusinessException);
    }

    private void stackTrace(Integer lineNumber) {
        if (this.isBusinessException()) {
            return;
        }
        if (this.exception != null) {
            this.stack = this.getStackTrace(this.exception.getStackTrace(), lineNumber);
        }
    }

    private String getStackTrace(StackTraceElement[] stack, Integer lineNumber) {
        if (stack == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        printStackTrace(stack, new PrintWriter(sw), lineNumber);
        return sw.toString();
    }

    private void printStackTrace(StackTraceElement[] stackTrace, PrintWriter printWriter, Integer lineNumber) {
        if (lineNumber != null) {
            for (int i = 0; i <= lineNumber; i++) {
                if (stackTrace.length > i) {
                    printWriter.println(stackTrace[i]);
                }
            }
        } else {
            for (StackTraceElement stackTraceEl : stackTrace) {
                printWriter.println(stackTraceEl);
            }
        }
    }

    private void showError() {
        if (this.isBusinessException()) {
            log.error("""
                                        
                    --------------------------------- Business exception  ---------------------------------
                    {}
                    ---------------------------------------------------------------------------------------""", this);
        } else if (this.numRowsStacktrace != null) {
            log.error("""
                                        
                    --------------------------------- App exception ---------------------------------------
                    {}
                    StackTrace:
                    {}
                    ---------------------------------------------------------------------------------------""", this, this.stack);
        } else {
            log.error("""
                                         
                    --------------------------------- App exception ---------------------------------------
                     {}""", this, exception);
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\n status=" + status + '\'' +
                ",\n title='" + title + '\'' +
                ",\n detail='" + detail + '\'' +
                ",\n methodHttp='" + methodHttp + '\'' +
                ",\n uri='" + path + '\'' +
                ",\n user='" + dataUser + '\'' +
                ",\n className='" + className + '\'' +
                ",\n method='" + methodName + '\'' +
                ",\n lineError=" + lineError +
                ",\n exceptionName='" + exceptionName + '\'' +
                '}';
    }
}