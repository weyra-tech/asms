package com.bdu.asms.alumni_service_management.security.services.academicpositionservice.impl;


import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.ResourceNotFoundException;
import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.IdGeneratorService;
import com.bdu.asms.alumni_service_management.security.entities.AcademicPosition;
import com.bdu.asms.alumni_service_management.security.repository.userrepository.AcademicPositionRepository;
import com.bdu.asms.alumni_service_management.security.services.academicpositionservice.services.AcademicPositionService;
import com.bdu.asms.alumni_service_management.security.utils.AuthenticatedUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class AcademicPositionServiceImpl implements AcademicPositionService {
    private final AcademicPositionRepository academicPositionRepository;
    private final IdGeneratorService idGeneratorService;
    private final AuthenticatedUserUtils authenticatedUserUtils;

    @Override
    public AcademicPosition createPosition(AcademicPosition position) {
        if (academicPositionRepository.existsByName(position.getName())) {
            throw new IllegalArgumentException("Position name already exists: " + position.getName());
        }

        position.setPublicId(idGeneratorService.generatePublicId("ACAD_POS"));
        position.setCreateTime(LocalDateTime.now());
        position.setCreateUserId(authenticatedUserUtils.getCurrentUserId());
        position.setStatus(1); // active by default
        return academicPositionRepository.save(position);
    }

    @Override
    public AcademicPosition updatePosition(String publicId, AcademicPosition updatedPosition) {
        AcademicPosition existing = academicPositionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPosition not found with publicId: " + publicId));

        existing.setName(updatedPosition.getName());
        existing.setUpdateUserId(updatedPosition.getUpdateUserId());
        existing.setUpdateTime(LocalDateTime.now());
        existing.setUpdateUserId(authenticatedUserUtils.getCurrentUserId());
        existing.setStatus(updatedPosition.getStatus());
        existing.setRemarks(updatedPosition.getRemarks());

        return academicPositionRepository.save(existing);
    }

    @Override
    public void deletePosition(String publicId) {
        AcademicPosition position = academicPositionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPosition not found with publicId: " + publicId));
        academicPositionRepository.delete(position);
    }

    @Override
    public AcademicPosition getPositionByPublicId(String publicId) {
        return academicPositionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicPosition not found with publicId: " + publicId));
    }

    @Override
    public List<AcademicPosition> getAllPositions() {
        return academicPositionRepository.findAll();
    }

    @Override
    public List<AcademicPosition> getActivePositions() {
        return academicPositionRepository.findAll().stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .toList();
    }
}
