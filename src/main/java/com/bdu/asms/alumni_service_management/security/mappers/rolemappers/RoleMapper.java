package com.bdu.asms.alumni_service_management.security.mappers.rolemappers;


import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    // Entity → ResponseDTO
    @Mapping(target = "parentRoleName", expression = "java(role.getParentRole() != null ? role.getParentRole().getName() : null)")
    @Mapping(target = "permissions", expression = "java(role.getPermissions().stream().map(p -> p.getName()).collect(java.util.stream.Collectors.toSet()))")
    RoleResponseDTO toResponseDTO(Role role);

    // CreateDTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true) // generated server-side
    @Mapping(target = "permissions", ignore = true) // handle in service
    @Mapping(target = "parentRole", ignore = true) // resolve in service
    Role toEntity(RoleCreateDTO dto);

    // UpdateDTO → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "permissions", ignore = true) // handle in service
    @Mapping(target = "parentRole", ignore = true)
    void updateEntityFromDTO(RoleUpdateDTO dto, @MappingTarget Role role);
}
