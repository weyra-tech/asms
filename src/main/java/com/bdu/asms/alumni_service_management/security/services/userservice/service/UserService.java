package com.bdu.asms.alumni_service_management.security.services.userservice.service;


import com.bdu.asms.alumni_service_management.security.dtos.userdtos.AdminPasswordResetRequestDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.PasswordChangeRequestDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.UserStatusUpdateRequestDTO;

public interface UserService {

    void changePassword(PasswordChangeRequestDTO request);
    void resetPasswordByAdmin(String userPublicId, AdminPasswordResetRequestDTO request);
    void updateUserStatus(String publicId, UserStatusUpdateRequestDTO request);

}