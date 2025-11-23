package com.bdu.asms.alumni_service_management.bussinesslogic.services.organizationalunitservice.impl;


import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitCreateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitHierarchyDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitResponseDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitUpdateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnitType;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.BadRequestException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.CircularReferenceException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.DuplicateResourceException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.ResourceNotFoundException;
import com.bdu.asms.alumni_service_management.bussinesslogic.mappers.organizationalunitmappers.OrganizationalUnitMapper;
import com.bdu.asms.alumni_service_management.bussinesslogic.repository.organizationalunitrepository.OrganizationalUnitRepository;
import com.bdu.asms.alumni_service_management.bussinesslogic.repository.organizationalunitrepository.OrganizationalUnitTypeRepository;
import com.bdu.asms.alumni_service_management.bussinesslogic.services.organizationalunitservice.services.OrganizationalUnitService;
import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.IdGeneratorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationalUnitServiceImpl implements OrganizationalUnitService {

    private final OrganizationalUnitRepository organizationalUnitRepository;
    private final OrganizationalUnitMapper organizationalUnitMapper;
    private final IdGeneratorService idGeneratorService;

    private final OrganizationalUnitTypeRepository organizationalUnitTypeRepository;

    @Override
    public OrganizationalUnitResponseDTO createOrganizationalUnit(OrganizationalUnitCreateDTO createDTO) {
        validateCreateDTO(createDTO);

        OrganizationalUnit entity = organizationalUnitMapper.toEntity(createDTO);
        entity.setPublicId(idGeneratorService.generatePublicId("ORG-"));

        // Resolve parent if provided via DTO field
        if (createDTO.getParentPublicId() != null && !createDTO.getParentPublicId().isBlank()) {
            OrganizationalUnit parent = organizationalUnitRepository.findByPublicId(createDTO.getParentPublicId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent organizational unit not found with public ID: " + createDTO.getParentPublicId()));
            entity.setParent(parent);
        } else {
            entity.setParent(null);
        }
        OrganizationalUnitType organizationalUnitType = organizationalUnitTypeRepository
                .findByPublicId(createDTO.getOrganizationalUnitTypePublicId())
                .orElseThrow(() -> new BadRequestException("OrganizationalUnitType '" + createDTO.getOrganizationalUnitTypePublicId() + "' not found"));
        entity.setUnitType(organizationalUnitType);
        normalizeFields(entity);
        validateParentRelationship(entity);
        validateUniqueConstraints(entity);

        OrganizationalUnit saved = organizationalUnitRepository.save(entity);
        return organizationalUnitMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationalUnitResponseDTO getOrganizationalUnitByPublicId(String publicId) {
        OrganizationalUnit unit = organizationalUnitRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organizational unit not found with public ID: " + publicId));
        return organizationalUnitMapper.toResponseDTO(unit);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationalUnitHierarchyDTO getOrganizationalUnitHierarchy(String publicId) {
        OrganizationalUnit unit = organizationalUnitRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organizational unit not found with public ID: " + publicId));
        return buildHierarchy(unit);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrganizationalUnitResponseDTO> searchUnits(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return organizationalUnitRepository.findAll(pageable).map(organizationalUnitMapper::toResponseDTO);
        }
        Page<OrganizationalUnit> page = organizationalUnitRepository
                .findByNameContainingIgnoreCaseOrAbbreviationContainingIgnoreCaseOrUnitEmailContainingIgnoreCase(
                        keyword, keyword, keyword, pageable);
        return page.map(organizationalUnitMapper::toResponseDTO);
    }

    @Override
    public OrganizationalUnitResponseDTO updateOrganizationalUnit(String publicId, OrganizationalUnitUpdateDTO updateDTO) {
        OrganizationalUnit existing = organizationalUnitRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organizational unit not found with public ID: " + publicId));

        validateUpdateDTO(updateDTO);

        // Apply field updates via mapper
        organizationalUnitMapper.updateEntityFromDTO(updateDTO, existing);

        // Parent update if provided (nullable allowed to detach)
        if (updateDTO.getParentPublicId() != null) {
            if (updateDTO.getParentPublicId().isBlank()) {
                existing.setParent(null);
            } else {
                OrganizationalUnit newParent = organizationalUnitRepository.findByPublicId(updateDTO.getParentPublicId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Parent organizational unit not found with public ID: " + updateDTO.getParentPublicId()));
                ensureNoCircular(existing, newParent);
                existing.setParent(newParent);
            }
        }

        normalizeFields(existing);
        validateParentRelationship(existing);
        validateUniqueConstraints(existing);

        OrganizationalUnit updated = organizationalUnitRepository.save(existing);
        return organizationalUnitMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteOrganizationalUnit(String publicId) {
        OrganizationalUnit unit = organizationalUnitRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organizational unit not found with public ID: " + publicId));

        if (!unit.getChildren().isEmpty()) {
            throw new BadRequestException("Cannot delete unit with child units. Remove or move children first.");
        }
        // Optional: prevent deletion if appointments exist
        if (unit.getAppointments() != null && !unit.getAppointments().isEmpty()) {
            throw new BadRequestException("Cannot delete unit with existing appointments.");
        }

        organizationalUnitRepository.delete(unit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationalUnitResponseDTO> getChildUnits(String parentPublicId) {
        return organizationalUnitRepository.findByParent_PublicId(parentPublicId).stream()
                .map(organizationalUnitMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationalUnitResponseDTO> getAllOrganizationalUnits() {
        return organizationalUnitRepository.findAll().stream()
                .map(organizationalUnitMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    @Override
    public OrganizationalUnitResponseDTO createRootOrganizationalUnit(OrganizationalUnitCreateDTO createDTO) {
        if (rootUnitExists()) {
            throw new BadRequestException("Root organizational unit already exists");
        }
        if (createDTO.getParentPublicId() != null && !createDTO.getParentPublicId().isBlank()) {
            throw new BadRequestException("Root organizational unit cannot have a parent");
        }
        // Enforce type
       // createDTO.setUnitType(OrganizationalUnitType.UNIVERSITY);

        OrganizationalUnit root = organizationalUnitMapper.toEntity(createDTO);
        root.setPublicId(idGeneratorService.generatePublicId("ORG-"));
        root.setParent(null);
        OrganizationalUnitType universityType = organizationalUnitTypeRepository
                .findByName("UNIVERSITY")
                .orElseThrow(() -> new BadRequestException("OrganizationalUnitType 'UNIVERSITY' not found"));
        root.setUnitType(universityType);

        normalizeFields(root);
        validateUniqueConstraints(root);

        OrganizationalUnit saved = organizationalUnitRepository.save(root);
        return organizationalUnitMapper.toResponseDTO(saved);
    }

    // ---------------- Helpers ----------------

    private boolean rootUnitExists() {
        return organizationalUnitRepository.findByParentIsNull().isPresent();
    }

    private void validateCreateDTO(OrganizationalUnitCreateDTO dto) {
        if (dto == null) throw new BadRequestException("Payload is required");
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("Organizational unit name is required");
        }
        if (dto.getAbbreviation() == null || dto.getAbbreviation().isBlank()) {
            throw new BadRequestException("Abbreviation is required");
        }

    }

    private void validateUpdateDTO(OrganizationalUnitUpdateDTO dto) {
        if (dto == null) throw new BadRequestException("Payload is required");
        // Name and abbreviation can be nullable for partial updates; mapper applies only non-nulls.
    }

    private void normalizeFields(OrganizationalUnit unit) {
        if (unit.getName() != null) unit.setName(unit.getName().trim());
        if (unit.getAbbreviation() != null) unit.setAbbreviation(unit.getAbbreviation().trim().toUpperCase(Locale.ROOT));
        if (unit.getUnitEmail() != null) unit.setUnitEmail(unit.getUnitEmail().trim().toLowerCase(Locale.ROOT));
        if (unit.getPhoneNumber() != null) unit.setPhoneNumber(unit.getPhoneNumber().trim());
    }

    private void validateParentRelationship(OrganizationalUnit unit) {
        if (unit.getParent() == null) return;
        ensureNoCircular(unit, unit.getParent());
    }

    private void ensureNoCircular(OrganizationalUnit child, OrganizationalUnit candidateParent) {
        if (candidateParent == null) return;
        if (child.getPublicId() != null && child.getPublicId().equals(candidateParent.getPublicId())) {
            throw new CircularReferenceException("Organizational unit cannot be its own parent");
        }
        OrganizationalUnit cursor = candidateParent;
        while (cursor != null) {
            if (cursor.getPublicId() != null && cursor.getPublicId().equals(child.getPublicId())) {
                throw new CircularReferenceException("Circular reference detected in organizational hierarchy");
            }
            cursor = cursor.getParent();
        }
    }

    private void validateUniqueConstraints(OrganizationalUnit unit) {
        // Name uniqueness scoped by parent
        if (unit.getParent() == null) {
            organizationalUnitRepository.findByNameIgnoreCaseAndParent_IsNull(unit.getName())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(unit.getId())) {
                            throw new DuplicateResourceException("A root-level unit with this name already exists");
                        }
                    });
        } else {
            organizationalUnitRepository.findByNameIgnoreCaseAndParent_PublicId(unit.getName(), unit.getParent().getPublicId())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(unit.getId())) {
                            throw new DuplicateResourceException("A unit with this name already exists under the same parent");
                        }
                    });
        }

        // Abbreviation uniqueness (global)
        if (unit.getAbbreviation() != null && !unit.getAbbreviation().isBlank()) {
            organizationalUnitRepository.findByAbbreviationIgnoreCase(unit.getAbbreviation())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(unit.getId())) {
                            throw new DuplicateResourceException("A unit with this abbreviation already exists");
                        }
                    });
        }

        // Email uniqueness (global)
        if (unit.getUnitEmail() != null && !unit.getUnitEmail().isBlank()) {
            organizationalUnitRepository.findByUnitEmailIgnoreCase(unit.getUnitEmail())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(unit.getId())) {
                            throw new DuplicateResourceException("A unit with this email already exists");
                        }
                    });
        }
    }

    private OrganizationalUnitHierarchyDTO buildHierarchy(OrganizationalUnit unit) {
        OrganizationalUnitHierarchyDTO dto = new OrganizationalUnitHierarchyDTO();
        dto.setPublicId(unit.getPublicId());
        dto.setName(unit.getName());
        dto.setAbbreviation(unit.getAbbreviation());
        dto.setUnitType(unit.getUnitType());
        dto.setChildren(unit.getChildren().stream()
                .map(this::buildHierarchy)
                .collect(Collectors.toList()));
        return dto;
    }

    @Transactional(readOnly = true)
    public  Set<OrganizationalUnit> getUnitAndChildren(String publicId) {
        OrganizationalUnit root = organizationalUnitRepository.findByPublicId(publicId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "OrganizationalUnit not found with publicId: " + publicId));

        Set<OrganizationalUnit> result = new HashSet<>();
        collectUnitAndChildren(root, result);
        return result;
    }

    /**
     * Recursive helper to collect OU and its children.
     */
    private  void collectUnitAndChildren(OrganizationalUnit unit, Set<OrganizationalUnit> result) {
        if (unit == null || result.contains(unit)) return;
        result.add(unit);

        if (unit.getChildren() != null) {
            for (OrganizationalUnit child : unit.getChildren()) {
                collectUnitAndChildren(child, result);
            }
        }
    }
}