package com.bdu.asms.alumni_service_management.security.dtos.roledtos;

import lombok.Data;

import java.util.Set;

@Data
public class RoleResponseDTO {
    private String publicId;
    private String name;
    private String description;
    private String parentRoleName;
    private Set<String> permissions; // permission names
}
