package com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos;


import com.bdu.asms.alumni_service_management.security.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentResponseDTO {
    private String publicId;
    private String staffFullName;
    private String positionName;
    private String organizationalUnitName;
    private LocalDate startDate;
    private LocalDate endDate;
    private AppointmentStatus appointmentStatus;

}
