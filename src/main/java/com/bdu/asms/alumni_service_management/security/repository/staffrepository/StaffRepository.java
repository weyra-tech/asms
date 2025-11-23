package com.bdu.asms.alumni_service_management.security.repository.staffrepository;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import com.bdu.asms.alumni_service_management.security.entities.Staff;
import com.bdu.asms.alumni_service_management.security.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StaffRepository extends JpaRepository<Staff,Long> {
    Optional<Staff> findByPublicId(String publicId);

    Optional<Staff> findByUserPublicId(String userPublicId);
    List<Staff> findByOrganizationalUnitPublicId(String orgUnitPublicId);
    List<Staff> findAllByOrganizationalUnitIn(Set<OrganizationalUnit> units);

    boolean existsByPublicId(String publicId);

    Optional<Staff>findByUser(User user);

    boolean existsByUser(User user);



        boolean existsByUserPublicId(String userPublicId);
        Page<Staff> findByOrganizationalUnit_PublicId(String unitPublicId, Pageable pageable);







        // Total distinct staff under OU scope
        @Query("""
        SELECT COUNT(DISTINCT s.id)
        FROM Staff s
        JOIN s.organizationalUnit u
        WHERE (:unitPublicIds IS NULL OR u.publicId IN :unitPublicIds)
        """)
        Long countDistinctStaffInUnits(@Param("unitPublicIds") Set<String> unitPublicIds);

        // Distinct staff that have at least one resource of the given type under OU scope
        @Query("""
        SELECT COUNT(DISTINCT s.id)
        FROM Staff s
        JOIN s.organizationalUnit u
        LEFT JOIN com.university.unicore.bussinesslogic.entities.staff.resource.StaffResource sr ON sr.staff = s
        LEFT JOIN sr.resourceType rt
        WHERE (:unitPublicIds IS NULL OR u.publicId IN :unitPublicIds)
          AND rt.publicId = :resourceTypePublicId
        """)
        Long countStaffWithResourceType(
                @Param("unitPublicIds") Set<String> unitPublicIds,
                @Param("resourceTypePublicId") String resourceTypePublicId
        );

    }










