package com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos;


import lombok.Data;

@Data
public class OrganizationalUnitCreateDTO {
    private String name;
    private String abbreviation;
    private String organizationalUnitTypePublicId;
    private String parentPublicId; // optional
    private String unitEmail;
    private String phoneNumber;
}
