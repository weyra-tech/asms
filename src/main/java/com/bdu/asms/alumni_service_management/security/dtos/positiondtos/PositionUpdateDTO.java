package com.bdu.asms.alumni_service_management.security.dtos.positiondtos;

import com.bdu.asms.alumni_service_management.security.enums.PositionLevel;

import lombok.Data;

@Data
public class PositionUpdateDTO {
    private String title;
    private String description;
    private PositionLevel level;

}
