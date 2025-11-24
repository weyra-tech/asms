package com.bdu.asms.alumni_service_management.bussinesslogic.repository.servicerepository;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_request.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    Optional<ServiceRequest> findByTrackingNumber(String trackingNumber);

    List<ServiceRequest> findByEmail(String email);

    List<ServiceRequest> findByStatus(ServiceRequest.RequestStatus status);

    boolean existsByTrackingNumber(String trackingNumber);
}
