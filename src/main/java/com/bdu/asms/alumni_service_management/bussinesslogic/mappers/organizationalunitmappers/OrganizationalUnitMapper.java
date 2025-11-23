package com.bdu.asms.alumni_service_management.bussinesslogic.mappers.organizationalunitmappers;

import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitCreateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitResponseDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitUpdateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;
@Mapper(componentModel = "spring")
public interface OrganizationalUnitMapper {

    // Entity → ResponseDTO
    @Mapping(target = "parentName", source = "parent.name")
    @Mapping(target = "childNames", source = "children", qualifiedByName = "mapChildrenToNames")
    OrganizationalUnitResponseDTO toResponseDTO(OrganizationalUnit unit);

    // CreateDTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    OrganizationalUnit toEntity(OrganizationalUnitCreateDTO dto);

    // UpdateDTO → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    void updateEntityFromDTO(OrganizationalUnitUpdateDTO dto, @MappingTarget OrganizationalUnit unit);

    @Named("mapChildrenToNames")
    default List<String> mapChildrenToNames(List<OrganizationalUnit> children) {
        if (children == null) {
            return List.of();
        }
        return children.stream()
                .map(OrganizationalUnit::getName)
                .collect(Collectors.toList());
    }

    @Named("mapParentName")
    default String mapParentName(OrganizationalUnit parent) {
        return parent != null ? parent.getName() : null;
    }
}