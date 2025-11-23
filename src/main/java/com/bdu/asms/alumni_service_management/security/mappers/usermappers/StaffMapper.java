package com.bdu.asms.alumni_service_management.security.mappers.usermappers;


import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Staff;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface StaffMapper {

    // Entity → ResponseDTO
    @Mapping(target = "fullName", expression = "java(staff.getFullName())")
    @Mapping(target = "user", source = "user") // nested mapping handled by UserMapper
    @Mapping(target = "organizationalUnitName", expression = "java(staff.getOrganizationalUnit() != null ? staff.getOrganizationalUnit().getName() : null)")
    StaffResponseDTO toResponseDTO(Staff staff);

    // CreateDTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true) // generated server-side
    @Mapping(target = "user", ignore = true) // must link separately
    @Mapping(target = "organizationalUnit", ignore = true) // resolve in service
    Staff toEntity(StaffCreateDTO dto);

    // UpdateDTO → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "organizationalUnit", ignore = true)
    void updateEntityFromDTO(StaffUpdateDTO dto, @MappingTarget Staff staff);
}
