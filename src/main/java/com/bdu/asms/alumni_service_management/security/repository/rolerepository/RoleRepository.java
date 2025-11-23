package com.bdu.asms.alumni_service_management.security.repository.rolerepository;


import com.bdu.asms.alumni_service_management.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByPublicId(String publicId);
    Optional<Role> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByParentRolePublicId(String publicId);
    boolean existsByPermissionsPublicId(String permissionPublicId);


}
