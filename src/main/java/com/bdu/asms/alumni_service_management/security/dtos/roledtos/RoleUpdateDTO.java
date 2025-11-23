package com.bdu.asms.alumni_service_management.security.dtos.roledtos;

import lombok.Data;

import java.util.Set;

@Data
public class RoleUpdateDTO {
    private String name;
    private String description;
    private Set<String> permissionPublicIds; // optional update
    private String parentRolePublicId; // optional update
}