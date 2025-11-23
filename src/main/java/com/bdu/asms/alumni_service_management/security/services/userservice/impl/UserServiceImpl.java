package com.bdu.asms.alumni_service_management.security.services.userservice.impl;

import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.ResourceNotFoundException;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.AdminPasswordResetRequestDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.PasswordChangeRequestDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.UserStatusUpdateRequestDTO;
import com.bdu.asms.alumni_service_management.security.entities.User;
import com.bdu.asms.alumni_service_management.security.repository.userrepository.UserRepository;
import com.bdu.asms.alumni_service_management.security.services.userservice.service.UserService;
import com.bdu.asms.alumni_service_management.security.utils.AuthenticatedUserUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticatedUserUtils authenticatedUserUtils;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(PasswordChangeRequestDTO request) {
        User user = authenticatedUserUtils.getCurrentUser();

        // 1️⃣ Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // 2️⃣ Encode and update
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdateUserId(authenticatedUserUtils.getCurrentUserId());
        userRepository.save(user);
    }

    @Transactional
    public void resetPasswordByAdmin(String userPublicId, AdminPasswordResetRequestDTO request) {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdateUserId(authenticatedUserUtils.getCurrentUserId());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }
    @Transactional
    public void updateUserStatus(String publicId, UserStatusUpdateRequestDTO request) {
        User user = userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with publicId: " + publicId));

        user.setUserStatus(request.getUserStatus());
        user.setUpdateUserId(authenticatedUserUtils.getCurrentUserId());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }




}