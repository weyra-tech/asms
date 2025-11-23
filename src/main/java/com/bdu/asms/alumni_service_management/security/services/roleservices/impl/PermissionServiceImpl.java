package com.bdu.asms.alumni_service_management.security.services.roleservices.impl;


import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.BadRequestException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.DuplicateResourceException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.ResourceNotFoundException;
import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.IdGeneratorService;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Permission;
import com.bdu.asms.alumni_service_management.security.mappers.permissionmapper.PermissionMapper;
import com.bdu.asms.alumni_service_management.security.repository.rolerepository.PermissionRepository;
import com.bdu.asms.alumni_service_management.security.repository.rolerepository.RoleRepository;
import com.bdu.asms.alumni_service_management.security.services.roleservices.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RoleRepository roleRepository;
    private final IdGeneratorService idGeneratorService;

    @Override
    public PermissionResponseDTO createPermission(PermissionCreateDTO dto) {
        String normalizedName = normalizeName(dto.getName());

        if (permissionRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException("Permission with name '" + normalizedName + "' already exists.");
        }

        Permission permission = permissionMapper.toEntity(dto);
        permission.setPublicId(idGeneratorService.generatePublicId("PERM-"));
        permission.setName(normalizedName);

        Permission saved = permissionRepository.save(permission);
        return permissionMapper.toResponseDTO(saved);
    }

    @Override
    public PermissionResponseDTO updatePermission(String publicId, PermissionUpdateDTO dto) {
        Permission existing = permissionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with publicId: " + publicId));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            String normalizedName = normalizeName(dto.getName());
            if (!normalizedName.equalsIgnoreCase(existing.getName())
                    && permissionRepository.existsByNameIgnoreCase(normalizedName)) {
                throw new DuplicateResourceException("Permission with name '" + normalizedName + "' already exists.");
            }
            existing.setName(normalizedName);
        }

        // If you have other fields (e.g., description), update them here:
        // if (dto.getDescription() != null) existing.setDescription(dto.getDescription());

        Permission updated = permissionRepository.save(existing);
        return permissionMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDTO getPermissionByPublicId(String publicId) {
        Permission permission = permissionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with publicId: " + publicId));
        return permissionMapper.toResponseDTO(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponseDTO> getPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(permissionMapper::toResponseDTO);
    }

    @Override
    public void deletePermission(String publicId) {
        Permission permission = permissionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with publicId: " + publicId));

        // Guard: prevent deleting a permission assigned to any role
        if (roleRepository.existsByPermissionsPublicId(publicId)) {
            throw new BadRequestException("Cannot delete permission that is assigned to one or more roles.");
        }

        permissionRepository.delete(permission);
    }

    // ---------------- Helpers ----------------

    private String normalizeName(String name) {
        if (name == null) throw new BadRequestException("Permission name must not be null");
        String n = name.trim();
        if (n.isBlank()) throw new BadRequestException("Permission name must not be blank");
        return n;
    }
}