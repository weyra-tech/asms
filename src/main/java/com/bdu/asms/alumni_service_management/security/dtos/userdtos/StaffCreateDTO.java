package com.bdu.asms.alumni_service_management.security.dtos.userdtos;


import com.bdu.asms.alumni_service_management.security.entities.AcademicPosition;
import com.bdu.asms.alumni_service_management.security.enums.GenderEnum;
import lombok.Data;

@Data
public class StaffCreateDTO {
    private String fName;
    private String mName;
    private String lName;
    private GenderEnum gender;

    private String nationality;
    private String phoneNumber;
    private String employmentId;

    private AcademicPosition academicPosition;

    private String profileSummary;
    private String thumbnail;
    private String organizationalUnitPublicId; // link to org unit
    private String userPublicId; // optional if creating separately
}
