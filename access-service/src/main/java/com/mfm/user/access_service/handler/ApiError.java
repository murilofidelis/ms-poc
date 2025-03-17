package com.mfm.user.access_service.handler;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ProblemDetail;

import java.time.Instant;

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

}
