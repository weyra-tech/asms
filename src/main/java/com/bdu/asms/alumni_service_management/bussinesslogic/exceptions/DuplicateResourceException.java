package com.bdu.asms.alumni_service_management.bussinesslogic.exceptions;


import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends AppException {
    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", HttpStatus.CONFLICT, message);
    }
}
