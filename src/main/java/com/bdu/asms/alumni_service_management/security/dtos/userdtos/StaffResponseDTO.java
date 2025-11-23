package com.bdu.asms.alumni_service_management.security.dtos.userdtos;


import com.bdu.asms.alumni_service_management.security.entities.AcademicPosition;
import com.bdu.asms.alumni_service_management.security.enums.GenderEnum;
import lombok.Data;

@Data
public class StaffResponseDTO {
    private String publicId;
    private String fullName;
    private GenderEnum gender;

    private String nationality;
    private String phoneNumber;
    private String employmentId;
    private AcademicPosition academicPosition;

    private String profileSummary;
    private String thumbnail;
    private String organizationalUnitName;
    private UserResponseDTO user; // nested user info
}
