package com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos;


import com.bdu.asms.alumni_service_management.security.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentCreateDTO {
    private String staffPublicId;
    private String positionPublicId;
    private String organizationalUnitPublicId;
    private LocalDate startDate;
    private LocalDate endDate;
    private AppointmentStatus appointmentStatus;

}
