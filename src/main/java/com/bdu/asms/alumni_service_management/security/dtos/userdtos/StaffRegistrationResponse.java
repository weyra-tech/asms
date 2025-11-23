package com.bdu.asms.alumni_service_management.security.dtos.userdtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StaffRegistrationResponse {
    private String publicId;
    private String fullName;
    private String userName;
    private String organizationalUnitName;
    private String role;
    private String status;
}

