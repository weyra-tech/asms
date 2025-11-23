package com.bdu.asms.alumni_service_management.bussinesslogic.services.organizationalunitservice.impl;



import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnitType;
import com.bdu.asms.alumni_service_management.bussinesslogic.repository.organizationalunitrepository.OrganizationalUnitTypeRepository;
import com.bdu.asms.alumni_service_management.bussinesslogic.services.organizationalunitservice.services.OrganizationalUnitTypeService;
import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.IdGeneratorService;
import com.bdu.asms.alumni_service_management.security.utils.AuthenticatedUserUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationalUnitTypeServiceImpl implements OrganizationalUnitTypeService {

    private final OrganizationalUnitTypeRepository repository;

    private final IdGeneratorService idGeneratorService;
    private final AuthenticatedUserUtils authenticatedUserUtils;

    @Override
    public OrganizationalUnitType create(OrganizationalUnitType unitType) {
        if (repository.existsByName(unitType.getName())) {
            throw new IllegalArgumentException("Organizational unit type name already exists: " + unitType.getName());
        }

        unitType.setPublicId(idGeneratorService.generatePublicId("ORG_UNIT_TYPE-"));
        unitType.setCreateTime(LocalDateTime.now());
        unitType.setCreateUserId(authenticatedUserUtils.getCurrentUserId());
        unitType.setStatus(1); // active by default
        return repository.save(unitType);
    }

    @Override
    public OrganizationalUnitType update(String publicId, OrganizationalUnitType updatedUnitType) {
        OrganizationalUnitType existing = repository.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("OrganizationalUnitType not found with publicId: " + publicId));

        existing.setName(updatedUnitType.getName());
        existing.setUpdateUserId(updatedUnitType.getUpdateUserId());
        existing.setUpdateTime(LocalDateTime.now());
        existing.setUpdateUserId(authenticatedUserUtils.getCurrentUserId());
        existing.setStatus(updatedUnitType.getStatus());
        existing.setRemarks(updatedUnitType.getRemarks());

        return repository.save(existing);
    }

    @Override
    public void delete(String publicId) {
        OrganizationalUnitType unitType = repository.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("OrganizationalUnitType not found with publicId: " + publicId));
        repository.delete(unitType);
    }

    @Override
    public OrganizationalUnitType getByPublicId(String publicId) {
        return repository.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("OrganizationalUnitType not found with publicId: " + publicId));
    }

    @Override
    public List<OrganizationalUnitType> getAll() {
        return repository.findAll();
    }


}

