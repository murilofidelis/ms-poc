package com.mfm.user.user_service.client.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.ProblemDetail;

import java.time.Instant;

@ToString

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class ApiError extends ProblemDetail {

    private String msgCod;

    private Instant timestamp;

    private String traceId;

}
