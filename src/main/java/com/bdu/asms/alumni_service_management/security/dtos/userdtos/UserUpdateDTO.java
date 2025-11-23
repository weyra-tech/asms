package com.bdu.asms.alumni_service_management.security.dtos.userdtos;

import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateDTO {
    private String userName;
    private String password;
    private Set<String> rolePublicIds;

}