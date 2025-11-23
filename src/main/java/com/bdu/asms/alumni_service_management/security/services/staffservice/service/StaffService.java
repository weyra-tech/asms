package com.bdu.asms.alumni_service_management.security.services.staffservice.service;


import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffRegistrationResponse;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffUpdateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.UserRegistrationRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface StaffService {

    StaffRegistrationResponse registerStaff(UserRegistrationRequestDTO req);
    StaffResponseDTO updateStaff(String publicId, StaffUpdateDTO dto);

    StaffResponseDTO getStaffByPublicId(String publicId);
    StaffResponseDTO getStaffByUserPublicId(String userPublicId);

    List<StaffResponseDTO> getAllStaff();                  // returns empty list if none
    Page<StaffResponseDTO> getStaff(Pageable pageable);    // paginated

    List<StaffResponseDTO> getStaffByOrganizationalUnit(String orgUnitPublicId); // returns empty list if none

    void deleteStaff(String publicId); // hard delete (or switch to soft delete if your entity supports it)

    // Only direct unit, no children
    Page<StaffResponseDTO> getStaffByUnit(String unitPublicId, Pageable pageable); // new method


    // Optional: expose if you add soft-delete on Staff
    // StaffResponseDTO reactivateStaff(String publicId);
}