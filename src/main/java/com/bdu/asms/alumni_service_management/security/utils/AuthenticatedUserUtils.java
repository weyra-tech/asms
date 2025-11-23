package com.bdu.asms.alumni_service_management.security.utils;


import com.bdu.asms.alumni_service_management.security.entities.User;

import com.bdu.asms.alumni_service_management.security.repository.userrepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserUtils {

    private final UserRepository userRepository;

    /**
     * Returns the currently authenticated username (email)
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName(); // typically your login email
    }

    /**
     * Returns the currently authenticated User entity
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) return null;

        return userRepository.findByUserName(username).orElse(null);
    }

    /**
     * Returns the current user ID
     */
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}
