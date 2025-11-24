package com.bdu.asms.alumni_service_management.bussinesslogic.dtos.servicesdtos.request;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_request.ServiceRequest;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceRequestResponseDTO {

    private Long id;
    private String trackingNumber;
    private ServiceRequest.RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Category Info
    private Long categoryId;
    private String categoryName;

    // --- Personal Information ---
    private String firstName;
    private String fatherName;
    private String grandfatherName;
    private String mobileNumber;
    private String email;
    private String studentId;

    // --- Amharic Names ---
    private String firstNameAmharic;
    private String fatherNameAmharic;
    private String grandfatherNameAmharic;

    // --- Academic Information ---
    private ServiceRequest.AdmissionType admissionType;
    private ServiceRequest.DegreeType degreeType;
    private String college;
    private String department;
    private String otherCollege;
    private String otherDepartment;
    private ServiceRequest.StudentStatus studentStatus;

    // --- Graduation Details ---
    private String graduationYearEC;
    private String graduationYearGC;

    // --- Request Specifics ---
    private ServiceRequest.OrderType orderType;
    private String destinationInstitution;
    private String destinationAddress;
    private String destinationCountry;
    private String mailingAgent;
}
