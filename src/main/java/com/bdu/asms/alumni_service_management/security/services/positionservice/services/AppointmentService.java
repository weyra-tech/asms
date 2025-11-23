package com.bdu.asms.alumni_service_management.security.services.positionservice.services;


import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO createAppointment(AppointmentCreateDTO dto);
    AppointmentResponseDTO updateAppointment(String publicId, AppointmentUpdateDTO dto);
    AppointmentResponseDTO getAppointmentById(String publicId);
    List<AppointmentResponseDTO> getAppointmentsByStaff(String staffPublicId);
    List<AppointmentResponseDTO> getAppointmentsByOrganizationalUnit(String unitPublicId);
    List<AppointmentResponseDTO> getAllAppointments();
    void endAppointment(String publicId);

    Page<AppointmentResponseDTO> getAppointments(Pageable pageable);


}
