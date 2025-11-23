package com.bdu.asms.alumni_service_management.bussinesslogic.repository.organizationalunitrepository;


import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationalUnitRepository extends JpaRepository<OrganizationalUnit,Long> {
Optional<OrganizationalUnit> findByPublicId(String publicId);
    Page<OrganizationalUnit> findByNameContainingIgnoreCaseOrUnitTypeContainingIgnoreCase(
            String name,
            String unitType,
            Pageable pageable
    );
    // Find all children of a parent unit (by parent's public ID)
    List<OrganizationalUnit> findByParentPublicId(String parentPublicId);
    // Find all units of a specific type
    //List<OrganizationalUnit> findByUnitType(OrganizationalUnitType unitType);
    // Find by name and parent ID (for uniqueness validation)
    Optional<OrganizationalUnit> findByNameAndParentId(String name, Long parentId);

    // Find by abbreviation (for uniqueness validation)
    Optional<OrganizationalUnit> findByAbbreviation(String abbreviation);

    // Find by unit email (for uniqueness validation)
    Optional<OrganizationalUnit> findByUnitEmail(String unitEmail);
    List<OrganizationalUnit> findByPublicIdIn(List<String> publicIds);
    Optional<OrganizationalUnit> findByParentIsNull();


    // Children by parent public id
    List<OrganizationalUnit> findByParent_PublicId(String parentPublicId);



    // Uniqueness helpers
    Optional<OrganizationalUnit> findByNameIgnoreCaseAndParent_IsNull(String name);
    Optional<OrganizationalUnit> findByNameIgnoreCaseAndParent_PublicId(String name, String parentPublicId);

    Optional<OrganizationalUnit> findByAbbreviationIgnoreCase(String abbreviation);
    Optional<OrganizationalUnit> findByUnitEmailIgnoreCase(String unitEmail);

    // Search
    Page<OrganizationalUnit> findByNameContainingIgnoreCaseOrAbbreviationContainingIgnoreCaseOrUnitEmailContainingIgnoreCase(
            String name, String abbreviation, String email, Pageable pageable
    );

    List<OrganizationalUnit> findByPublicIdIn(Collection<String> publicIds);


    @Query("""
    SELECT u FROM OrganizationalUnit u
    LEFT JOIN FETCH u.children
    WHERE u.publicId = :publicId
""")
    Optional<OrganizationalUnit> findByPublicIdWithChildren(@Param("publicId") String publicId);

}
