package com.bdu.asms.alumni_service_management.security.config.entity;



import com.bdu.asms.alumni_service_management.security.entities.User;
import com.bdu.asms.alumni_service_management.security.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom implementation of Spring Security's UserDetails.
 * Wraps the application's User entity to integrate with Spring Security.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = (user != null) ? user : new User();
    }

    /**
     * Convert roles and permissions into Spring Security authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                if (role.getAuthorities() != null) {
                    authorities.addAll(role.getAuthorities());
                }
            });
        }
        return authorities;
    }

    /**
     * Return the wrapped user entity for reference elsewhere (e.g., in controllers).
     */
    public User getUser() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can add expiration logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        // Only locked if explicitly BLOCKED
        return user.getUserStatus() != UserStatus.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Modify if password rotation is required
    }

    @Override
    public boolean isEnabled() {
        // Only ACTIVE users can log in
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    // Convenience accessors
    public Long getId() {
        return user.getId();
    }

    public String getPublicId() {
        return user.getPublicId();
    }

    @JsonIgnore
    public UserStatus getStatus() {
        return user.getUserStatus();
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "username='" + getUsername() + '\'' +
                ", status=" + user.getStatus() +
                ", roles=" + (user.getRoles() != null ? user.getRoles().size() : 0) +
                '}';
    }
}
