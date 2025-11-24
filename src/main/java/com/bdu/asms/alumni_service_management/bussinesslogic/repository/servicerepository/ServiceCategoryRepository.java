package com.bdu.asms.alumni_service_management.bussinesslogic.repository.servicerepository;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_category.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    Optional<ServiceCategory> findByName(String name);

    List<ServiceCategory> findByIsActiveTrue();
}
