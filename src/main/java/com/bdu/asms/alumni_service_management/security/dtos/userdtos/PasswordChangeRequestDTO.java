package com.bdu.asms.alumni_service_management.security.dtos.userdtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class PasswordChangeRequestDTO {

    @NotBlank(message = "Old password is required")
    private String oldPassword;
    @NotBlank(message = "new password is required")
    private String newPassword;
}
