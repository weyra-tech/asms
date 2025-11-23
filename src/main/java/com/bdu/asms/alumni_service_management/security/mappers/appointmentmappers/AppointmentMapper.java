package com.bdu.asms.alumni_service_management.security.mappers.appointmentmappers;


import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Appointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "staffFullName", expression = "java(appointment.getStaff().getFullName())")
    @Mapping(target = "positionName", expression = "java(appointment.getPosition().getName())")
    @Mapping(target = "organizationalUnitName", expression = "java(appointment.getOrganizationalUnit().getName())")
    AppointmentResponseDTO toResponseDTO(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "staff", ignore = true) // resolve in service using staffPublicId
    @Mapping(target = "position", ignore = true) // resolve in service
    @Mapping(target = "organizationalUnit", ignore = true) // resolve in service

    Appointment toEntity(AppointmentCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "staff", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "organizationalUnit", ignore = true)
    void updateEntityFromDTO(AppointmentUpdateDTO dto, @MappingTarget Appointment appointment);
}
