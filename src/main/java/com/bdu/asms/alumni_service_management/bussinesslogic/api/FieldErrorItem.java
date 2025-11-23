package com.bdu.asms.alumni_service_management.bussinesslogic.api;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldErrorItem {
    private String field;          // e.g., "title"
    private String message;        // e.g., "must not be blank"
    private Object rejectedValue;  // optional: the invalid value
}
