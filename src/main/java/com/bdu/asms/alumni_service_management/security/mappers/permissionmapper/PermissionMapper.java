package com.bdu.asms.alumni_service_management.security.mappers.permissionmapper;


import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Permission;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    // Entity → ResponseDTO
    PermissionResponseDTO toResponseDTO(Permission permission);

    // CreateDTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true) // generated server-side
    Permission toEntity(PermissionCreateDTO dto);

    // UpdateDTO → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(PermissionUpdateDTO dto, @MappingTarget Permission permission);
}
