package com.mfm.user.access_service.handler;

import com.mfm.user.access_service.util.Message;
import com.mfm.user.access_service.util.PackageClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.mfm.user.access_service.filter.AppFilter.TRACE_ID;

@Slf4j
public class ApplicationErrorHandler {

    private final Message message;
    private final Set<Class<?>> businessExceptions;

    public ApplicationErrorHandler(Message message, String[] businessExceptionsPackageScan) {
        log.info("Create ApplicationErrorHandler");
        this.message = message;
        this.businessExceptions = new HashSet<>();
        this.loadExceptionsClasses(businessExceptionsPackageScan);
    }

    private void loadExceptionsClasses(String[] packsScan) {
        for (String pack : packsScan) {
            Set<Class<?>> loadedClasses = PackageClassLoader.findAllClassesUsingClassLoader(pack);
            if (loadedClasses == null || loadedClasses.isEmpty()) {
                return;
            }
            loadedClasses.forEach(loadClass -> {
                boolean isException = Exception.class.isAssignableFrom(loadClass);
                if (isException) {
                    businessExceptions.add(loadClass);
                }
            });
        }
    }

    public ApiError buildError(Exception exception, NativeWebRequest request) {

        AppException appException = getAppExceptionAnnotation(exception);
        HttpStatus status = getStatus(appException);
        String msgCod = Optional.ofNullable(appException).map(AppException::msgCod).orElse(null);

        String title = getTitle(status);
        URI instance = getInstance(request);
        String detail = getDetails(msgCod, exception);
        String traceId = MDC.get(TRACE_ID);

        this.createLog(status, msgCod, exception, request);

        ApiError apiError = new ApiError();
        apiError.setMsgCod(msgCod);
        apiError.setStatus(status.value());
        apiError.setTitle(title);
        apiError.setInstance(instance);
        apiError.setTimestamp(Instant.now());
        apiError.setTraceId(traceId);
        apiError.setDetail(detail);
        return apiError;
    }

    private String getDetails(String msgCod, Exception exception) {
        if (msgCod != null) {
            String messageDetail = this.message.get(msgCod);
            if (messageDetail != null) {
                return messageDetail;
            }
        }
        return exception.getMessage();
    }

    private AppException getAppExceptionAnnotation(Exception exception) {
        return exception.getClass().getAnnotation(AppException.class);
    }

    private URI getInstance(NativeWebRequest request) {
        String requestURI = ((ServletWebRequest) request).getRequest().getRequestURI();
        return URI.create(requestURI);
    }

    private String getTitle(HttpStatus status) {
        return status.getReasonPhrase();
    }

    private HttpStatus getStatus(AppException appException) {
        if (appException != null) {
            return HttpStatus.valueOf(appException.httpStatusCod());
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private void createLog(HttpStatus status, String msgCod, Exception exception, NativeWebRequest request) {

        String title = this.getTitle(status);
        String statusCode = String.valueOf(status.value());
        String methodHttp = this.getHttpMethod(request);
        String path = this.getInstance(request).getPath();
        String user = "null";
        String detail = this.getDetails(msgCod, exception);
        String exceptionMessageDetail = exception.getMessage();
        var stackTraceDetails = this.getStackTraceDetails(exception);

        String logDetails = "{" +
                "\n status=" + statusCode + '\'' +
                ",\n title='" + title + '\'' +
                ",\n methodHttp='" + methodHttp + '\'' +
                ",\n uri='" + path + '\'' +
                ",\n user='" + user + '\'' +
                ",\n detail='" + detail + '\'' +
                ",\n exceptionMessageDetail='" + exceptionMessageDetail + '\'' +
                ",\n className='" + stackTraceDetails.className() + '\'' +
                ",\n method='" + stackTraceDetails.methodName() + '\'' +
                ",\n line=" + stackTraceDetails.lineError() + '\'' +
                ",\n exceptionName='" + stackTraceDetails.exceptionName() + '\'' +
                ",\n cause='" + stackTraceDetails.cause() + '\'' +
                "\n}";
        this.printLog(logDetails, exception);
    }

    private void printLog(String logDetails, Exception exception) {
        if (this.isBusinessException(exception)) {
            log.error("""
                                        
                    --------------------------------- Business exception  ---------------------------------
                    {}
                    ---------------------------------------------------------------------------------------""", logDetails);
        } else {
            log.error("""
                                        
                    --------------------------------- Api exception ---------------------------------------
                    {}
                    StackTrace:
                    ---------------------------------------------------------------------------------------""", logDetails, exception);
        }
    }

    private boolean isBusinessException(Exception exception) {
        return (exception != null && this.businessExceptions.contains(exception.getClass()));
    }

    private String getHttpMethod(NativeWebRequest request) {
        return ((ServletWebRequest) request).getRequest().getMethod();
    }

    private StackTraceDetails getStackTraceDetails(Exception exception) {
        String exceptionName = null;
        String className = null;
        String methodName = null;
        Integer lineError = null;
        String cause = null;
        StackTraceElement[] stackTrace = null;
        if (exception != null) {
            stackTrace = exception.getStackTrace();
            exceptionName = exception.getClass().getName();
            cause = Optional.ofNullable(exception.getCause()).map(Object::getClass).map(Class::getName).orElse("");
        }
        if (stackTrace != null && stackTrace.length > 0) {
            StackTraceElement stackTraceElement = stackTrace[0];
            className = stackTraceElement.getClassName();
            methodName = stackTraceElement.getMethodName();
            lineError = stackTraceElement.getLineNumber();
        }
        return new StackTraceDetails(className, exceptionName, methodName, lineError, cause);
    }

    private record StackTraceDetails(String className,
                                     String exceptionName,
                                     String methodName,
                                     Integer lineError,
                                     String cause) {

    }

}
