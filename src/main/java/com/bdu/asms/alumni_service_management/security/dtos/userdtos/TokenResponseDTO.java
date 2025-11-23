package com.bdu.asms.alumni_service_management.security.dtos.userdtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDTO {
    private String token;
    private String tokenType;   // "Bearer"
    private long    expiresIn;  // milliseconds (or seconds if you prefer)
}