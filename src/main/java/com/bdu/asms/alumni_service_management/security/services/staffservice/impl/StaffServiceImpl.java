package com.bdu.asms.alumni_service_management.security.services.staffservice.impl;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.BadRequestException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.DuplicateResourceException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.ResourceNotFoundException;
import com.bdu.asms.alumni_service_management.bussinesslogic.repository.organizationalunitrepository.OrganizationalUnitRepository;
import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.IdGeneratorService;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.*;
import com.bdu.asms.alumni_service_management.security.entities.AcademicPosition;
import com.bdu.asms.alumni_service_management.security.entities.Role;
import com.bdu.asms.alumni_service_management.security.entities.Staff;
import com.bdu.asms.alumni_service_management.security.entities.User;
import com.bdu.asms.alumni_service_management.security.enums.UserStatus;
import com.bdu.asms.alumni_service_management.security.mappers.usermappers.StaffMapper;
import com.bdu.asms.alumni_service_management.security.repository.rolerepository.RoleRepository;
import com.bdu.asms.alumni_service_management.security.repository.staffrepository.StaffRepository;
import com.bdu.asms.alumni_service_management.security.repository.userrepository.AcademicPositionRepository;
import com.bdu.asms.alumni_service_management.security.repository.userrepository.UserRepository;
import com.bdu.asms.alumni_service_management.security.services.staffservice.service.StaffService;
import com.bdu.asms.alumni_service_management.security.utils.AuthenticatedUserUtils;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
@Service
@Transactional
@AllArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final OrganizationalUnitRepository orgUnitRepository;
    private final StaffMapper staffMapper;
    private final IdGeneratorService idGeneratorService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final AcademicPositionRepository academicPositionRepository;

    private final AuthenticatedUserUtils authenticatedUserUtils;;


    // In your StaffService (or the service where registerStaff lives)
// Add @Transactional at method level if not already at class level
    @Transactional
    public StaffRegistrationResponse registerStaff(UserRegistrationRequestDTO req) {
        // 1) Validate required fields
        if (req.getUserName() == null || req.getUserName().isBlank())
            throw new BadRequestException("Username (email) is required");
        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new BadRequestException("Password is required");
        if (req.getOrganizationalUnitPublicId() == null || req.getOrganizationalUnitPublicId().isBlank())
            throw new BadRequestException("Organizational Unit publicId is required");
        if (req.getFName() == null || req.getFName().isBlank())
            throw new BadRequestException("First name is required");
        if (req.getLName() == null || req.getLName().isBlank())
            throw new BadRequestException("Last name is required");

        // 2) Normalize username and enforce uniqueness
        String normalizedUsername = req.getUserName().trim().toLowerCase();
        userRepository.findByUserName(normalizedUsername).ifPresent(u -> {
            throw new DuplicateResourceException("A user with the provided username already exists");
        });

        // 3) Fetch Organizational Unit
        OrganizationalUnit orgUnit = orgUnitRepository.findByPublicId(req.getOrganizationalUnitPublicId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organizational Unit not found with publicId: " + req.getOrganizationalUnitPublicId()
                ));

       AcademicPosition academicPosition = academicPositionRepository.findByPublicId(req.getAcademicPositionPublicId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic Position not found with publicId: " + req.getAcademicPositionPublicId()
                ));

        // 4) Create and persist User
        User user = new User();
        user.setPublicId(idGeneratorService.generatePublicId("USR-"));
        user.setUserName(normalizedUsername);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUserStatus(UserStatus.ACTIVE);
        user.setCreateTime(LocalDateTime.now());

        Role defaultRole = roleRepository.findByNameIgnoreCase("STAFF")
                .orElseThrow(() -> new ResourceNotFoundException("Default role 'STAFF' not found"));
        user.getRoles().add(defaultRole);

        userRepository.save(user);

        // 5) Create and persist Staff profile
        Staff staff = new Staff();
        staff.setPublicId(idGeneratorService.generatePublicId("STF-"));
        staff.setUser(user);
        staff.setFName(req.getFName());
        staff.setStatus(1);
        staff.setMName(req.getMName());
        staff.setLName(req.getLName());
        staff.setGender(req.getGender());
        staff.setPhoneNumber(req.getPhoneNumber());
        staff.setEmploymentId(req.getEmploymentId());
        staff.setProfileSummary(req.getProfileSummary());
        staff.setNationality(req.getNationality());
        staff.setOrganizationalUnit(orgUnit);
        staff.setAcademicPosition(academicPosition);
        staff.setCreateTime(LocalDateTime.now());
        // If this endpoint is admin-only, you can set createUserId from the authenticated user
         staff.setCreateUserId(authenticatedUserUtils.getCurrentUserId());

        staffRepository.save(staff);

        // 6) Return response (fixed builder usage)
        return StaffRegistrationResponse.builder()
                .publicId(staff.getPublicId())
                .fullName(staff.getFullName())
                .userName(user.getUserName())
                .organizationalUnitName(orgUnit.getName())
                .role(defaultRole.getName())
                .status("ACTIVE")
                .build();
    }

    
    @Override
    public StaffResponseDTO updateStaff(String publicId, StaffUpdateDTO dto) {
        Staff staff = staffRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Staff not found with publicId: " + publicId
                ));

        // Partial updates
        staffMapper.updateEntityFromDTO(dto, staff);

        // Update org unit if provided
        if (dto.getAcademicPositionPublicId() != null && !dto.getAcademicPositionPublicId().isBlank()) {
            AcademicPosition position = academicPositionRepository.findByPublicId(dto.getAcademicPositionPublicId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Academic Position  not found with publicId: " + dto.getAcademicPositionPublicId()
                    ));
            staff.setAcademicPosition(position);
        }

        Staff updatedStaff = staffRepository.save(staff);
        return staffMapper.toResponseDTO(updatedStaff);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponseDTO getStaffByPublicId(String publicId) {
        Staff staff = staffRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Staff not found with publicId: " + publicId
                ));
        return staffMapper.toResponseDTO(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponseDTO getStaffByUserPublicId(String userPublicId) {
        Staff staff = staffRepository.findByUserPublicId(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Staff not found for user with publicId: " + userPublicId
                ));
        return staffMapper.toResponseDTO(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponseDTO> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();
        // Return empty list if none
        return staffList.stream()
                .map(staffMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StaffResponseDTO> getStaff(Pageable pageable) {
        Page<Staff> page = staffRepository.findAll(pageable);
        return page.map(staffMapper::toResponseDTO);
    }

    @Override
    public List<StaffResponseDTO> getStaffByOrganizationalUnit(String orgUnitPublicId) {
        List<Staff> staffList = staffRepository.findByOrganizationalUnitPublicId(orgUnitPublicId);
        // Return empty list if none
        return staffList.stream()
                .map(staffMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStaff(String publicId) {
        Staff staff = staffRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Staff not found with publicId: " + publicId
                ));

        // Business rule check before delete
        if (hasActiveAssignments(staff)) {
            throw new BadRequestException(
                    "Cannot delete staff member with active assignments. PublicId: " + publicId
            );
        }

        // If you switch to soft delete later, implement here
        staffRepository.delete(staff);
    }



    // ---------------- Validation & business helpers ----------------

    private void validateStaffCreation(StaffCreateDTO dto) {
        if (dto.getUserPublicId() == null || dto.getUserPublicId().trim().isEmpty()) {
            throw new BadRequestException("User publicId is required");
        }
        if (dto.getOrganizationalUnitPublicId() == null || dto.getOrganizationalUnitPublicId().trim().isEmpty()) {
            throw new BadRequestException("Organizational Unit publicId is required");
        }
    }

    // Placeholder: add your own checks (appointments, course loads, etc.)
    private boolean hasActiveAssignments(Staff staff) {
        // e.g., return appointmentRepository.existsByStaffPublicIdAndActiveTrue(staff.getPublicId());
        return false;
    }

    // Optional: Implement if you add soft-delete fields to Staff entity
    // public StaffResponseDTO reactivateStaff(String publicId) { ... }


    private String normalizeUsername(String userName) {
        if (userName == null) throw new BadRequestException("Username must not be null");
        String normalized = userName.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            throw new BadRequestException("Username must not be blank");
        }
        return normalized;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StaffResponseDTO> getStaffByUnit(String unitPublicId, Pageable pageable) {
        Page<Staff> page= staffRepository.findByOrganizationalUnit_PublicId(unitPublicId, pageable);
        return page.map(staffMapper::toResponseDTO);
    }


}