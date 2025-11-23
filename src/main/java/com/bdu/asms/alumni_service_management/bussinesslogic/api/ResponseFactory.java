package com.bdu.asms.alumni_service_management.bussinesslogic.api;



import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@UtilityClass
public class ResponseFactory {

    // ---------------- Success ----------------

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return build(HttpStatus.OK, "OK", "Success", data, null, null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(URI location, T data) {
        ApiResponse<T> body = baseSuccess("CREATED", "Resource created", data, null);
        enrich(body);
        if (location != null) {
            return ResponseEntity.created(location).body(body);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public static ResponseEntity<ApiResponse<Void>> noContent() {
        ApiResponse<Void> body = baseSuccess("NO_CONTENT", "No content", null, null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(enrich(body));
    }

    public static <T> ResponseEntity<ApiResponse<List<T>>> list(List<T> data) {
        return build(HttpStatus.OK, "OK", "Success", data, null, null);
    }

    public static <T> ResponseEntity<ApiResponse<List<T>>> paged(Page<T> page) {
        PaginationMeta meta = PaginationMeta.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(page.getSort() != null ? page.getSort().toString() : null)
                .build();
        return build(HttpStatus.OK, "OK", "Success", page.getContent(), null, meta);
    }

    // ---------------- Errors ----------------

    public static ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String code, String message) {
        return error(status, code, message, (ApiError) null);
    }

    public static ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String code, String message, ApiError apiError) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(message)
                .error(apiError != null ? apiError : ApiError.builder().code(code).message(message).build())
                .timestamp(LocalDateTime.now())
                .build();
        enrich(body);
        return ResponseEntity.status(status).body(body);
    }

    // Helper to lazily build ApiError
    public static ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String code, String message, Supplier<ApiError> errorSupplier) {
        ApiError apiError = errorSupplier != null ? errorSupplier.get() : null;
        return error(status, code, message, apiError);
    }

    // ---------------- Internals ----------------

    private static <T> ResponseEntity<ApiResponse<T>> build(HttpStatus status, String code, String message, T data, ApiError error, PaginationMeta meta) {
        ApiResponse<T> body = ApiResponse.<T>builder()
                .success(error == null)
                .code(code)
                .message(message)
                .data(error == null ? data : null)
                .error(error)
                .meta(meta)
                .timestamp(LocalDateTime.now())
                .build();
        enrich(body);
        return ResponseEntity.status(status).body(body);
    }

    private static <T> ApiResponse<T> baseSuccess(String code, String message, T data, PaginationMeta meta) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .meta(meta)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Enrich with path and requestId if available
    private static <T> ApiResponse<T> enrich(ApiResponse<T> body) {
        HttpServletRequest req = currentRequest();
        if (req != null) {
            body.setPath(req.getRequestURI());

            // Try common request/correlation id headers
            String requestId = firstNonBlank(
                    req.getHeader("X-Request-Id"),
                    req.getHeader("X-Request-ID"),
                    req.getHeader("X-Correlation-Id"),
                    req.getHeader("X-Correlation-ID")
            );

            // As a last resort, try W3C traceparent: version-traceid-parentid-flags
            if (requestId == null || requestId.isBlank()) {
                String traceparent = req.getHeader("traceparent");
                String traceId = traceIdFromTraceparent(traceparent);
                if (traceId != null && !traceId.isBlank()) {
                    requestId = traceId;
                }
            }

            body.setRequestId(requestId);
        }
        return body;
    }

    private static HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest();
        }
        return null;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String traceIdFromTraceparent(String traceparent) {
        if (traceparent == null || traceparent.isBlank()) return null;
        // Expected format: "00-<32hexTraceId>-<16hexParentId>-<flags>"
        String[] parts = traceparent.split("-");
        return (parts.length >= 2) ? parts[1] : null;
    }
}