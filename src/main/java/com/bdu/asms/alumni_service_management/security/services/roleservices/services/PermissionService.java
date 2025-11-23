package com.bdu.asms.alumni_service_management.security.services.roleservices.services;

import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    PermissionResponseDTO createPermission(PermissionCreateDTO dto);
    PermissionResponseDTO updatePermission(String publicId, PermissionUpdateDTO dto);
    PermissionResponseDTO getPermissionByPublicId(String publicId);
    List<PermissionResponseDTO> getAllPermissions();
    void deletePermission(String publicId);



    Page<PermissionResponseDTO> getPermissions(Pageable pageable);


}
