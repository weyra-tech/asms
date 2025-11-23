package com.bdu.asms.alumni_service_management.security.dtos.userdtos;

import com.bdu.asms.alumni_service_management.security.enums.GenderEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationRequestDTO {

    @NotBlank
    @Email
    private String userName;
    @NotBlank
    private String password;

    @JsonProperty("fName")
    private String fName;
    @JsonProperty("mName")
    private String mName;
    @JsonProperty("lName")
    private String lName;
    private GenderEnum gender;

    private String nationality;
    private String phoneNumber;
    private String employmentId;

    private String academicPositionPublicId;

    private String profileSummary;
    private String thumbnail;
    private String organizationalUnitPublicId; // link to org unit
}



