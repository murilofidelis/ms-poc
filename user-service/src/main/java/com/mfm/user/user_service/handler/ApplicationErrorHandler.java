package com.mfm.user.user_service.handler;

import com.mfm.user.user_service.util.JsonUtil;
import com.mfm.user.user_service.util.Message;
import com.mfm.user.user_service.util.PackageClassLoader;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.mfm.user.user_service.filter.AppFilter.TRACE_ID;

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
                    this.businessExceptions.add(loadClass);
                }
            });
        }
    }

    public ApiError buildError(Exception exception, WebRequest request) {

        if (exception instanceof MethodArgumentNotValidException ex) {
            return buildMethodArgumentNotValidExceptionResponse(request, ex);
        }

        ApiError apiError = getApiErrorFromFeignResponse(exception);

        URI instance = getInstance(request);

        if (existsApiErrorFromFeignException(apiError)) {
            apiError.setInstance(instance);
        } else {
            AppException appException = getAppExceptionAnnotation(exception);
            HttpStatus status = getStatus(appException, exception);
            String msgCod = Optional.ofNullable(appException).map(AppException::msgCod).orElse(null);

            String title = getTitle(status);

            String detail = getDetails(msgCod, exception);
            String traceId = MDC.get(TRACE_ID);

            apiError = new ApiError();
            apiError.setMsgCod(msgCod);
            apiError.setStatus(status.value());
            apiError.setTitle(title);
            apiError.setInstance(instance);
            apiError.setTimestamp(Instant.now());
            apiError.setTraceId(traceId);
            apiError.setDetail(detail);

        }

        this.createLog(HttpStatus.valueOf(apiError.getStatus()), apiError.getMsgCod(), exception, request);

        return apiError;
    }

    private static boolean existsApiErrorFromFeignException(ApiError apiError) {
        return apiError != null && (apiError.getMsgCod() != null || apiError.getErrors() != null);
    }

    private ApiError buildMethodArgumentNotValidExceptionResponse(WebRequest request, MethodArgumentNotValidException ex) {
        List<ApiErrorFieldDetail> errors = new ArrayList<>();

        List<ObjectError> allErrors = ex.getAllErrors();

        for (ObjectError error : allErrors) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ApiErrorFieldDetail(fieldName, errorMessage));
        }

        URI instance = getInstance(request);
        String traceId = MDC.get(TRACE_ID);

        ApiError apiError = new ApiError();

        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        apiError.setInstance(instance);
        apiError.setTimestamp(Instant.now());
        apiError.setTraceId(traceId);
        apiError.setErrors(errors);
        return apiError;
    }

    private ApiError getApiErrorFromFeignResponse(Exception exception) {
        try {
            if (exception instanceof FeignException.FeignClientException feignException) {
                Optional<ByteBuffer> responseBody = feignException.responseBody();
                if (responseBody.isEmpty()) {
                    return null;
                }
                ByteBuffer responseBuffer = responseBody.get();
                if (!responseBuffer.hasArray()) {
                    return null;
                }
                byte[] bytes = new byte[responseBuffer.remaining()];
                if (bytes.length < 1) {
                    return null;
                }
                responseBuffer.get(bytes);
                String body = new String(bytes);
                return JsonUtil.toObject(body, ApiError.class);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
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

    private URI getInstance(WebRequest request) {
        String requestURI = ((ServletWebRequest) request).getRequest().getRequestURI();
        return URI.create(requestURI);
    }

    private String getTitle(HttpStatus status) {
        return status.getReasonPhrase();
    }

    private HttpStatus getStatus(AppException appException, Exception exception) {
        if (appException != null) {
            return HttpStatus.valueOf(appException.httpStatusCod());
        }
        if (exception instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        }
        if (exception instanceof FeignException.FeignClientException ex) {
            return HttpStatus.valueOf((ex).status());
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private void createLog(HttpStatus status, String msgCod, Exception exception, WebRequest request) {

        String title = this.getTitle(status);
        String statusCode = String.valueOf(status.value());
        String methodHttp = this.getHttpMethod(request);
        String path = this.getInstance(request).getPath();
        String user = this.getUser(request);
        String detail = this.getDetails(msgCod, exception);
        String exceptionMessageDetail = exception.getMessage();
        StackTraceDetails stackTraceDetails = this.getStackTraceDetails(exception);

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

        this.printLog(msgCod, logDetails, exception);
    }

    private String getUser(WebRequest request) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) request.getUserPrincipal();
        if (jwtAuthenticationToken == null) {
            return null;
        }
        Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim("name");
    }

    private void printLog(String msgCod, String logDetails, Exception exception) {
        if (this.isBusinessException(msgCod, exception)) {
            log.error("""
                                        
                    --------------------------------- Business exception  ---------------------------------
                    {}
                    ---------------------------------------------------------------------------------------""", logDetails);
        } else {
            log.error("""
                                        
                    --------------------------------- Api exception ---------------------------------------
                    {}
                    ---------------------------------------------------------------------------------------""", logDetails, exception);
        }
    }

    private boolean isBusinessException(String msgCod, Exception exception) {
        return (msgCod != null) ||
                (exception != null && this.businessExceptions.contains(exception.getClass()) ||
                        (exception instanceof MethodArgumentNotValidException));
    }

    private String getHttpMethod(WebRequest request) {
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
