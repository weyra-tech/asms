package com.bdu.asms.alumni_service_management.security.services.roleservices.impl;


import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.BadRequestException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.CircularReferenceException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.DuplicateResourceException;
import com.bdu.asms.alumni_service_management.bussinesslogic.exceptions.ResourceNotFoundException;
import com.bdu.asms.alumni_service_management.bussinesslogic.utils.commonutils.IdGeneratorService;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleUpdateDTO;
import com.bdu.asms.alumni_service_management.security.entities.Permission;
import com.bdu.asms.alumni_service_management.security.entities.Role;
import com.bdu.asms.alumni_service_management.security.mappers.rolemappers.RoleMapper;
import com.bdu.asms.alumni_service_management.security.repository.rolerepository.PermissionRepository;
import com.bdu.asms.alumni_service_management.security.repository.rolerepository.RoleRepository;
import com.bdu.asms.alumni_service_management.security.repository.userrepository.UserRepository;
import com.bdu.asms.alumni_service_management.security.services.roleservices.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;
    private final IdGeneratorService idGeneratorService;

    @Override
    public RoleResponseDTO createRole(RoleCreateDTO dto) {
        // Validate name
        String normalizedName = normalizeName(dto.getName());
        if (roleRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException("Role with name '" + normalizedName + "' already exists.");
        }

        Role role = roleMapper.toEntity(dto);
        role.setPublicId(idGeneratorService.generatePublicId("ROLE-"));
        role.setName(normalizedName);

        // Parent role
        if (dto.getParentRolePublicId() != null && !dto.getParentRolePublicId().isBlank()) {
            Role parent = roleRepository.findByPublicId(dto.getParentRolePublicId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent role not found with publicId: " + dto.getParentRolePublicId()));
            ensureNoCircularParent(role, parent);
            role.setParentRole(parent);
        }

        // Permissions
        role.setPermissions(resolvePermissions(dto.getPermissionPublicIds()));

        Role saved = roleRepository.save(role);
        return roleMapper.toResponseDTO(saved);
    }

    @Override
    public RoleResponseDTO updateRole(String publicId, RoleUpdateDTO dto) {
        Role existing = roleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with publicId: " + publicId));

        // Optional name update
        if (dto.getName() != null && !dto.getName().isBlank()) {
            String normalizedName = normalizeName(dto.getName());
            if (!normalizedName.equalsIgnoreCase(existing.getName())
                    && roleRepository.existsByNameIgnoreCase(normalizedName)) {
                throw new DuplicateResourceException("Role with name '" + normalizedName + "' already exists.");
            }
            existing.setName(normalizedName);
        }

        // Description update (nullable allowed)
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        // Parent role update
        if (dto.getParentRolePublicId() != null) {
            if (dto.getParentRolePublicId().isBlank()) {
                existing.setParentRole(null);
            } else {
                Role parent = roleRepository.findByPublicId(dto.getParentRolePublicId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Parent role not found with publicId: " + dto.getParentRolePublicId()));
                ensureNoCircularParent(existing, parent);
                existing.setParentRole(parent);
            }
        }

        // Permissions update (if provided)
        if (dto.getPermissionPublicIds() != null) {
            existing.setPermissions(resolvePermissions(dto.getPermissionPublicIds()));
        }

        Role updated = roleRepository.save(existing);
        return roleMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleByPublicId(String publicId) {
        Role role = roleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with publicId: " + publicId));
        return roleMapper.toResponseDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> getRoles(Pageable pageable) {
        Page<Role> page = roleRepository.findAll(pageable);
        return page.map(roleMapper::toResponseDTO);
    }

    @Override
    public void deleteRole(String publicId) {
        Role role = roleRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with publicId: " + publicId));

        // Prevent deletion if assigned to users
        // Requires: boolean existsByRolesPublicId(String publicId) on UserRepository (Spring Data derived query)
        if (userRepository.existsByRolesPublicId(publicId)) {
            throw new BadRequestException("Cannot delete role assigned to one or more users.");
        }

        // Prevent deletion if has child roles
        // Requires: boolean existsByParentRolePublicId(String publicId) on RoleRepository
        if (roleRepository.existsByParentRolePublicId(publicId)) {
            throw new BadRequestException("Cannot delete role that has child roles. Reassign or remove children first.");
        }

        roleRepository.delete(role);
    }

    // ---------------- Helpers ----------------

    private String normalizeName(String name) {
        if (name == null) throw new BadRequestException("Role name must not be null");
        String n = name.trim();
        if (n.isBlank()) throw new BadRequestException("Role name must not be blank");
        return n;
    }

    private Set<Permission> resolvePermissions(Set<String> permissionPublicIds) {
        if (permissionPublicIds == null || permissionPublicIds.isEmpty()) return new HashSet<>();

        Set<Permission> resolved = new HashSet<>();
        List<String> missing = new ArrayList<>();

        for (String pid : permissionPublicIds) {
            if (pid == null || pid.isBlank()) continue;
            permissionRepository.findByPublicId(pid.trim())
                    .ifPresentOrElse(resolved::add, () -> missing.add(pid));
        }

        if (!missing.isEmpty()) {
            throw new ResourceNotFoundException("The following permissions were not found: " + missing);
        }
        return resolved;
    }

    private void ensureNoCircularParent(Role child, Role candidateParent) {
        if (candidateParent == null) return;

        // Self-parenting check
        if (sameRole(child, candidateParent)) {
            throw new CircularReferenceException("Role cannot be its own parent");
        }

        // Ascend the parent chain of candidateParent and ensure 'child' is not an ancestor
        Role cursor = candidateParent;
        // Note: relies on active transaction to traverse lazy parentRole
        while (cursor != null) {
            if (sameRole(child, cursor)) {
                throw new CircularReferenceException(
                        "Circular role hierarchy detected: '" + child.getName() + "' is an ancestor of '" + candidateParent.getName() + "'"
                );
            }
            cursor = cursor.getParentRole();
        }
    }

    private boolean sameRole(Role a, Role b) {
        if (a == null || b == null) return false;
        if (a.getId() != null && b.getId() != null) {
            return Objects.equals(a.getId(), b.getId());
        }
        // Fallback on publicId if JPA id not available
        return a.getPublicId() != null && a.getPublicId().equals(b.getPublicId());
    }
}