package com.bdu.asms.alumni_service_management.security.dtos.userdtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank
    @Email
    private String userName;

    @NotBlank
    private String password;


}

