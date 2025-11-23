package com.bdu.asms.alumni_service_management.security.repository.positionrepository;



import com.bdu.asms.alumni_service_management.security.entities.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByNameIgnoreCase(String title);
    Optional<Position> findByPublicId(String publicId);
}
