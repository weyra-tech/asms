package com.bdu.asms.alumni_service_management.bussinesslogic.exceptions;


import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends AppException {
    public ServiceUnavailableException(String message) {
        super("SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}