package com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationalUnitHierarchyDTO {

    private Long id;
    private String publicId;
    private String name;
    private String abbreviation;
    private String unitEmail;
    private String phoneNumber;
    private OrganizationalUnitType unitType;
    private List<OrganizationalUnitHierarchyDTO> children; // Recursive structure for hierarchy
}