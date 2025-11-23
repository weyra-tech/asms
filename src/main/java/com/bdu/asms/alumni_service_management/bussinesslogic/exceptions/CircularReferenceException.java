package com.bdu.asms.alumni_service_management.bussinesslogic.exceptions;

import org.springframework.http.HttpStatus;

public class CircularReferenceException extends AppException {
    public CircularReferenceException(String message) {
        super("CIRCULAR_REFERENCE", HttpStatus.BAD_REQUEST, message);
    }
}


