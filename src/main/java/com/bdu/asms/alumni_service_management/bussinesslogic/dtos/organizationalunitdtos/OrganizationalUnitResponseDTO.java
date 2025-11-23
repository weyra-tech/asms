package com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnitType;
import lombok.Data;

import java.util.List;

@Data
public class OrganizationalUnitResponseDTO {
    private String publicId;
    private String name;
    private String abbreviation;
    private OrganizationalUnitType unitType;
    private String parentName;
    private String unitEmail;
    private String phoneNumber;
    private List<String> childNames; // list of immediate children
}
