package com.bdu.asms.alumni_service_management.security.repository.rolerepository;



import com.bdu.asms.alumni_service_management.security.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PermissionRepository extends JpaRepository<Permission, Long> {
        Optional<Permission> findByPublicId(String publicId);
        boolean existsByNameIgnoreCase(String name);

}
