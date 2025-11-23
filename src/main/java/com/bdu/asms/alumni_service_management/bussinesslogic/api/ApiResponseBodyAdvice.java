package com.bdu.asms.alumni_service_management.bussinesslogic.api;



import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Optional: Automatically wraps plain controller responses into ApiResponse.
 * Controllers returning ResponseEntity<ApiResponse<...>> from ResponseFactory are left untouched.
 */
@RestControllerAdvice(basePackages = "com.university.unicore")
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true; // evaluate in beforeBodyWrite to skip special cases
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // ✅ Skip if already wrapped or manually controlled
        if (body instanceof ApiResponse<?> || body instanceof ResponseEntity<?>)
            return body;

        // ✅ Skip wrapping file or binary responses
        if (body instanceof org.springframework.core.io.Resource)
            return body;

        // ✅ Skip raw string responses
        if (body instanceof String)
            return body;

        // ✅ Default: wrap in ApiResponse
        return ApiResponse.builder()
                .success(true)
                .code("OK")
                .message("Success")
                .data(body)
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getURI().getPath())
                .build();
    }

}
