package com.bdu.asms.alumni_service_management.security.config.service;


import com.bdu.asms.alumni_service_management.security.config.entity.CustomUserDetails;
import com.bdu.asms.alumni_service_management.security.entities.User;
import com.bdu.asms.alumni_service_management.security.repository.userrepository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println("DEBUG: Attempting login for username: " + username);
        System.out.println("DEBUG: Password in DB (encoded): " + user.getPassword());

        // ✅ Return your CustomUserDetails, not Spring's default User
        return new CustomUserDetails(user);
    }


}
