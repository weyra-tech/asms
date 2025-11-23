package com.bdu.asms.alumni_service_management.security.dtos.positiondtos;


import com.bdu.asms.alumni_service_management.security.enums.PositionLevel;
import lombok.Data;

@Data
public class PositionResponseDTO {
    private String publicId;
    private String title;
    private String description;
    private PositionLevel level;

}
