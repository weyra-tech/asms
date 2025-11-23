package com.bdu.asms.alumni_service_management.security.services.positionservice.impl;


import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.BadRequestException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.ResourceNotFoundException;
import com.bdu.asms.alumni_service_management.bussinesslogic.repository.organizationalunitrepository.OrganizationalUnitRepository;
import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.IdGeneratorService;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Appointment;
import com.bdu.asms.alumni_service_management.security.entities.Position;
import com.bdu.asms.alumni_service_management.security.entities.Staff;
import com.bdu.asms.alumni_service_management.security.enums.AppointmentStatus;
import com.bdu.asms.alumni_service_management.security.mappers.appointmentmappers.AppointmentMapper;
import com.bdu.asms.alumni_service_management.security.repository.positionrepository.AppointmentRepository;
import com.bdu.asms.alumni_service_management.security.repository.positionrepository.PositionRepository;
import com.bdu.asms.alumni_service_management.security.repository.staffrepository.StaffRepository;
import com.bdu.asms.alumni_service_management.security.services.positionservice.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;




@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final StaffRepository staffRepository;
    private final PositionRepository positionRepository;
    private final OrganizationalUnitRepository organizationalUnitRepository;
    private final AppointmentMapper appointmentMapper;
    private final IdGeneratorService idGeneratorService;

    @Override
    public AppointmentResponseDTO createAppointment(AppointmentCreateDTO dto) {
        // Validate required IDs
        if (dto.getStaffPublicId() == null || dto.getStaffPublicId().isBlank()) {
            throw new BadRequestException("staffPublicId is required");
        }
        if (dto.getPositionPublicId() == null || dto.getPositionPublicId().isBlank()) {
            throw new BadRequestException("positionPublicId is required");
        }
        if (dto.getOrganizationalUnitPublicId() == null || dto.getOrganizationalUnitPublicId().isBlank()) {
            throw new BadRequestException("organizationalUnitPublicId is required");
        }

        // Fetch related entities
        Staff staff = staffRepository.findByPublicId(dto.getStaffPublicId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with public ID: " + dto.getStaffPublicId()));

        Position position = positionRepository.findByPublicId(dto.getPositionPublicId())
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with public ID: " + dto.getPositionPublicId()));

        OrganizationalUnit unit = organizationalUnitRepository.findByPublicId(dto.getOrganizationalUnitPublicId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizational unit not found with public ID: " + dto.getOrganizationalUnitPublicId()));

        // Validate dates
        LocalDate start = dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now();
        if (dto.getEndDate() != null && dto.getEndDate().isBefore(start)) {
            throw new BadRequestException("End date cannot be before start date.");
        }

        // Prevent multiple active appointments for same staff
        appointmentRepository.findByStaff_PublicIdAndAppointmentStatus(dto.getStaffPublicId(),AppointmentStatus.ACTIVE)
                .ifPresent(existing -> {
                    // Only block if the new appointment would be ACTIVE at creation
                    AppointmentStatus effectiveStatus = computeInitialStatus(dto.getAppointmentStatus(), start);
                    if (effectiveStatus == AppointmentStatus.ACTIVE) {
                        throw new BadRequestException("Staff already has an active appointment: " + existing.getPosition().getName());
                    }
                });

        // Map and set relations
        Appointment appointment = appointmentMapper.toEntity(dto);
        appointment.setPublicId(idGeneratorService.generatePublicId("APT-"));
        appointment.setStaff(staff);
        appointment.setPosition(position);
        appointment.setOrganizationalUnit(unit);
        appointment.setStartDate(start);
        appointment.setAppointmentStatus(computeInitialStatus(dto.getAppointmentStatus(), start));

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(saved);
    }

    @Override
    public AppointmentResponseDTO updateAppointment(String publicId, AppointmentUpdateDTO dto) {
        Appointment existing = appointmentRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with public ID: " + publicId));

        // Prevent modifications to terminated appointments
        if (existing.getAppointmentStatus() == AppointmentStatus.TERMINATED) {
            throw new BadRequestException("Cannot modify a terminated appointment.");
        }

        // Decide candidate start date for validation
        LocalDate candidateStart = dto.getStartDate() != null ? dto.getStartDate() : existing.getStartDate();

        // Date validation
        if (dto.getEndDate() != null && dto.getEndDate().isBefore(candidateStart)) {
            throw new BadRequestException("End date cannot be before start date.");
        }

        // Update start date: only if appointment is still pending
        if (dto.getStartDate() != null) {
            if (existing.getAppointmentStatus() != AppointmentStatus.PENDING) {
                throw new BadRequestException("Start date can only be changed while appointment is PENDING.");
            }
            existing.setStartDate(dto.getStartDate());
        }

        // Update end date and status transition if necessary
        if (dto.getEndDate() != null) {
            existing.setEndDate(dto.getEndDate());
            if (existing.getAppointmentStatus() == AppointmentStatus.ACTIVE && !dto.getEndDate().isAfter(LocalDate.now())) {
                existing.setAppointmentStatus(AppointmentStatus.EXPIRED);
            }
        }

        // Handle status transitions
        if (dto.getAppointmentStatus() != null) {
            AppointmentStatus newStatus = dto.getAppointmentStatus();

            // No changes if status is same
            if (!Objects.equals(existing.getAppointmentStatus(), newStatus)) {
                // Prevent re-activating if another active appointment exists for this staff
                if (newStatus == AppointmentStatus.ACTIVE) {
                    appointmentRepository.findByStaff_PublicIdAndAppointmentStatus(existing.getStaff().getPublicId(),AppointmentStatus.ACTIVE)
                            .ifPresent(other -> {
                                if (!Objects.equals(other.getId(), existing.getId())) {
                                    throw new BadRequestException("Staff already has another active appointment.");
                                }
                            });
                }

                // Basic transition rules
                if (existing.getAppointmentStatus() == AppointmentStatus.EXPIRED && newStatus == AppointmentStatus.ACTIVE) {
                    throw new BadRequestException("Cannot activate an expired appointment.");
                }
                if (existing.getAppointmentStatus() == AppointmentStatus.TERMINATED) {
                    throw new BadRequestException("Cannot change status of a terminated appointment.");
                }

                existing.setAppointmentStatus(newStatus);
            }
        }

        Appointment updated = appointmentRepository.save(existing);
        return appointmentMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(String publicId) {
        Appointment appointment = appointmentRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with public ID: " + publicId));
        return appointmentMapper.toResponseDTO(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByStaff(String staffPublicId) {
        // Ensure staff exists
        staffRepository.findByPublicId(staffPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with public ID: " + staffPublicId));

        return appointmentRepository.findByStaffPublicId(staffPublicId)
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByOrganizationalUnit(String unitPublicId) {
        // Ensure org unit exists
        organizationalUnitRepository.findByPublicId(unitPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizational unit not found with public ID: " + unitPublicId));

        return appointmentRepository.findByOrganizationalUnitPublicId(unitPublicId)
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> getAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(appointmentMapper::toResponseDTO);
    }

    @Override
    public void endAppointment(String publicId) {
        Appointment appointment = appointmentRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with public ID: " + publicId));

        if (appointment.getAppointmentStatus() == AppointmentStatus.TERMINATED ||
                appointment.getAppointmentStatus() == AppointmentStatus.EXPIRED) {
            throw new BadRequestException("Appointment already ended.");
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(appointment.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date.");
        }

        appointment.setEndDate(today);
        appointment.setAppointmentStatus(AppointmentStatus.TERMINATED);
        appointmentRepository.save(appointment);
    }



    // ---------------- Helpers ----------------

    private AppointmentStatus computeInitialStatus(AppointmentStatus requested, LocalDate startDate) {
        if (requested != null) return requested;
        // If start date is in the future, it's PENDING; otherwise ACTIVE
        return startDate.isAfter(LocalDate.now()) ? AppointmentStatus.PENDING : AppointmentStatus.ACTIVE;
    }
}