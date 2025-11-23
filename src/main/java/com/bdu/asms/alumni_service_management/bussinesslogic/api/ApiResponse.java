package com.bdu.asms.alumni_service_management.bussinesslogic.api;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;          // true for success, false for errors
    private String code;              // application-level code, e.g. "OK", "COURSE_CREATED", "VALIDATION_FAILED"
    private String message;           // human readable message
    private T data;                   // payload (generic)
    private ApiError error;           // error details (present when success=false)
    private PaginationMeta meta;      // optional metadata (pagination, etc.)
    private LocalDateTime timestamp;  // server time
    private String path;              // request path
    private String requestId;         // correlation/request id
}
