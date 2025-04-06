package com.mfm.user.user_service.handler;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ProblemDetail;

import java.time.Instant;
import java.util.List;

/**
 * custom implementation RFC-9457
 */

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class ApiError extends ProblemDetail {

    private String msgCod;

    private Instant timestamp;

    private String traceId;

    private List<ApiErrorFieldDetail> errors;

}
