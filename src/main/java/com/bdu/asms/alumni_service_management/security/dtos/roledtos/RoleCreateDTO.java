package com.bdu.asms.alumni_service_management.security.dtos.roledtos;

import lombok.Data;

import java.util.Set;

@Data
public class RoleCreateDTO {
    private String name;
    private String description;
    private Set<String> permissionPublicIds; // assign permissions by publicId
    private String parentRolePublicId; // optional
}
