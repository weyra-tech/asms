package com.bdu.asms.alumni_service_management.security.repository.userrepository;


import com.bdu.asms.alumni_service_management.security.entities.AcademicPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicPositionRepository extends JpaRepository<AcademicPosition,Long> {

    Optional<AcademicPosition> findByPublicId(String publicId);
    Optional<AcademicPosition> findByName(String name);
    boolean existsByName(String name);
}
