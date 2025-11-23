package com.bdu.asms.alumni_service_management.bussinesslogic.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class AppException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final Map<String, Object> details; // optional extra context

    public AppException(String code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
        this.details = null;
    }

    public AppException(String code, HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
        this.details = null;
    }

    public AppException(String code, HttpStatus status, String message, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.status = status;
        this.details = details;
    }
}