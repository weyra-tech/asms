package com.bdu.asms.alumni_service_management.security.mappers.positionmappers;


import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Position;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    PositionResponseDTO toResponseDTO(Position position);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true) // generated server-side
    Position toEntity(PositionCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(PositionUpdateDTO dto, @MappingTarget Position position);
}