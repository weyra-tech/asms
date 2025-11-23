package com.bdu.asms.alumni_service_management.bussinesslogic.api;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private String code;                        // e.g., "VALIDATION_FAILED", "NOT_FOUND"
    private String message;                     // high-level message
    private List<FieldErrorItem> fieldErrors;   // validation errors per field
    private Map<String, Object> details;        // additional context (optional)
}
