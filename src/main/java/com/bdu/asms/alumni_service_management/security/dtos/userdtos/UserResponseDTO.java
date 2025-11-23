package com.bdu.asms.alumni_service_management.security.dtos.userdtos;


import com.bdu.asms.alumni_service_management.security.enums.UserStatus;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDTO {
    private String publicId;
    private String userName;
    private UserStatus userStatus;
    private Set<String> roles;
}
