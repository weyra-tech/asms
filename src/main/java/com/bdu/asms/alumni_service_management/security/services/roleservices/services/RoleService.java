package com.bdu.asms.alumni_service_management.security.services.roleservices.services;

import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleUpdateDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleResponseDTO createRole(RoleCreateDTO dto);
    RoleResponseDTO updateRole(String publicId, RoleUpdateDTO dto);

    RoleResponseDTO getRoleByPublicId(String publicId);

    List<RoleResponseDTO> getAllRoles();
    Page<RoleResponseDTO> getRoles(Pageable pageable);

    void deleteRole(String publicId);
}
