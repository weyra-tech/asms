package com.bdu.asms.alumni_service_management.security.mappers.usermappers;


import com.bdu.asms.alumni_service_management.security.dtos.userdtos.UserCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.UserResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.UserUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity → ResponseDTO
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet()))")
    UserResponseDTO toResponseDTO(User user);

    // CreateDTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true) // server generates
    @Mapping(target = "userStatus", expression = "java(com.bdu.asms.alumni_service_management.security.enums.UserStatus.ACTIVE)")
    @Mapping(target = "roles", ignore = true) // handle in service
    @Mapping(target = "deleted", constant = "false")
    User toEntity(UserCreateDTO dto);

    // UpdateDTO → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true) // handle in service
    void updateEntityFromDTO(UserUpdateDTO dto, @MappingTarget User user);
}
