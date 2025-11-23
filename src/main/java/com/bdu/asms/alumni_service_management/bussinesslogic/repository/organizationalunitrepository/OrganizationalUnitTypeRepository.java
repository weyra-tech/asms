package com.bdu.asms.alumni_service_management.bussinesslogic.repository.organizationalunitrepository;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnitType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationalUnitTypeRepository extends JpaRepository<OrganizationalUnitType, Long> {
    Optional<OrganizationalUnitType> findByName(String name);
    Optional<OrganizationalUnitType> findByPublicId(String publicId);
    boolean existsByName(String name);
}
