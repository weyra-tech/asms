package com.bdu.asms.alumni_service_management.security.dtos.userdtos;


import com.bdu.asms.alumni_service_management.security.enums.UserStatus;
import lombok.Data;

@Data
public class UserStatusUpdateRequestDTO {
    private UserStatus userStatus;
}
