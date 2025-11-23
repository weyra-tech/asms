package com.bdu.asms.alumni_service_management.security.services.userservice.service;



import com.bdu.asms.alumni_service_management.security.config.entity.CustomUserDetails;
import com.bdu.asms.alumni_service_management.security.config.util.JwtTokenUtil;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.AuthRequestDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.MeResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.TokenResponseDTO;
import com.bdu.asms.alumni_service_management.security.entities.Appointment;
import com.bdu.asms.alumni_service_management.security.entities.Role;
import com.bdu.asms.alumni_service_management.security.entities.Staff;
import com.bdu.asms.alumni_service_management.security.entities.User;

import com.bdu.asms.alumni_service_management.security.enums.AppointmentStatus;
import com.bdu.asms.alumni_service_management.security.repository.positionrepository.AppointmentRepository;
import com.bdu.asms.alumni_service_management.security.repository.staffrepository.StaffRepository;
import com.bdu.asms.alumni_service_management.security.repository.userrepository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final AppointmentRepository appointmentRepository;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    public TokenResponseDTO login(@Valid AuthRequestDTO request) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName().trim().toLowerCase(),
                            request.getPassword()
                    )
            );
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

        SecurityContextHolder.getContext().setAuthentication(auth);

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        String token = jwtTokenUtil.generateToken(principal);

        return new TokenResponseDTO(token, "Bearer", jwtExpirationMs);
    }

    public MeResponseDTO getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            return null;
        }

        User user = principal.getUser();

        // Build user info
        List<String> roleNames = user.getRoles().stream().map(Role::getName).toList();
        List<String> authorities = principal.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        MeResponseDTO.UserProfileDTO userProfile = MeResponseDTO.UserProfileDTO.builder()
                .publicId(user.getPublicId())
                .userName(user.getUserName())
                .roles(roleNames)
                .authorities(authorities)
                .build();

        // Staff & Appointment
        Staff staff = staffRepository.findByUserPublicId(user.getPublicId()).orElse(null);

        MeResponseDTO.StaffProfileDTO staffProfile = null;
        MeResponseDTO.AppointmentDTO appointmentDTO = null;

        if (staff != null) {
            var orgUnit = staff.getOrganizationalUnit();
            var orgBrief = orgUnit != null
                    ? MeResponseDTO.OrgUnitBriefDTO.builder()
                    .publicId(orgUnit.getPublicId())
                    .name(orgUnit.getName())
                    .unitTypeName(orgUnit.getUnitType()!=null ?  orgUnit.getUnitType().getName() : null)
                    .build()
                    : null;

            staffProfile = MeResponseDTO.StaffProfileDTO.builder()
                    .publicId(staff.getPublicId())
                    .fName(staff.getFName())
                    .lName(staff.getLName())
                    .fullName(staff.getFullName())
                    .organizationalUnit(orgBrief)
                    .build();

            Optional<Appointment> active = appointmentRepository
                    .findByStaff_PublicIdAndAppointmentStatus(staff.getPublicId(), AppointmentStatus.ACTIVE);

            if (active.isPresent()) {
                Appointment a = active.get();

                var positionBrief = MeResponseDTO.PositionBriefDTO.builder()
                        .publicId(a.getPosition().getPublicId())
                        .title(a.getPosition().getName())
                        .build();

                var apptOrgBrief = MeResponseDTO.OrgUnitBriefDTO.builder()
                        .publicId(a.getOrganizationalUnit().getPublicId())
                        .name(a.getOrganizationalUnit().getName())
                        .build();

                appointmentDTO = MeResponseDTO.AppointmentDTO.builder()
                        .publicId(a.getPublicId())
                        .status(a.getAppointmentStatus().name())
                        .startDate(a.getStartDate())
                        .endDate(a.getEndDate())
                        .position(positionBrief)
                        .organizationalUnit(apptOrgBrief)
                        .build();
            }
        }

        return MeResponseDTO.builder()
                .user(userProfile)
                .staff(staffProfile)
                .currentAppointment(appointmentDTO)
                .build();
    }
}

