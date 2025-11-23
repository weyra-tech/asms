package com.bdu.asms.alumni_service_management.bussinesslogic.exceptions;


import com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiError;
import com.bdu.asms.alumni_service_management.bussinesslogic.api.FieldErrorItem;
import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // One place to handle all custom AppException subclasses
    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException ex) {
        ApiError apiError = ApiError.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .build();

        return ResponseFactory.error(ex.getStatus(), ex.getCode(), ex.getMessage(), apiError);
    }

    // Validation errors (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<FieldErrorItem> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorItem)
                .toList();

        ApiError apiError = ApiError.builder()
                .code("VALIDATION_FAILED")
                .message("Request validation failed")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseFactory.error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request validation failed", apiError);
    }

    // Spring 404 for non-existent endpoints
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        return ResponseFactory.error(HttpStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND",
                "The requested endpoint does not exist: " + request.getRequestURI());
    }

    // Access denied (security)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseFactory.error(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "You don't have permission to access this resource");
    }

    // Catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        ex.printStackTrace(); // replace with proper logging
        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred");
    }

    private FieldErrorItem toFieldErrorItem(FieldError fe) {
        return FieldErrorItem.builder()
                .field(fe.getField())
                .message(fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value")
                .rejectedValue(fe.getRejectedValue())
                .build();
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageError(StorageException ex) {
        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
}