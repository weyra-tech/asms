package com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_request;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_category.ServiceCategory;
import com.bdu.asms.alumni_service_management.bussinesslogic.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory serviceCategory;

    // --- Personal Information (Common) ---
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "father_name", nullable = false)
    private String fatherName;

    @Column(name = "grandfather_name")
    private String grandfatherName;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private String email;

    @Column(name = "student_id")
    private String studentId;

    // --- Amharic Names (Specific to Original Degree) ---
    @Column(name = "first_name_amharic")
    private String firstNameAmharic;

    @Column(name = "father_name_amharic")
    private String fatherNameAmharic;

    @Column(name = "grandfather_name_amharic")
    private String grandfatherNameAmharic;

    // --- Academic Information (Common) ---
    @Enumerated(EnumType.STRING)
    @Column(name = "admission_type", nullable = false)
    private AdmissionType admissionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree_type", nullable = false)
    private DegreeType degreeType;

    @Column(nullable = false)
    private String college;

    @Column(nullable = false)
    private String department;

    @Column(name = "other_college")
    private String otherCollege;

    @Column(name = "other_department")
    private String otherDepartment;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_status")
    private StudentStatus studentStatus;

    // --- Graduation Details (Specific to Original Degree) ---
    @Column(name = "graduation_year_ec")
    private String graduationYearEC;

    @Column(name = "graduation_year_gc")
    private String graduationYearGC;

    // --- Request Specifics (Specific to Transcript) ---
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;

    @Column(name = "destination_institution")
    private String destinationInstitution;

    @Column(name = "destination_address", columnDefinition = "TEXT")
    private String destinationAddress;

    @Column(name = "destination_country")
    private String destinationCountry;

    @Column(name = "mailing_agent")
    private String mailingAgent;

    // --- System Fields ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(name = "tracking_number", unique = true, nullable = false)
    private String trackingNumber;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    // --- Enums ---
    public enum RequestStatus {
        SUBMITTED, PENDING_PAYMENT, PAID, IN_REVIEW, NEEDS_CORRECTION, APPROVED, REJECTED, COMPLETED
    }

    public enum AdmissionType {
        REGULAR, EVENING, SUMMER, DISTANCE
    }

    public enum DegreeType {
        DIPLOMA, DEGREE, MASTERS, PHD, SP_CERTIFICATE
    }

    public enum StudentStatus {
        ACTIVE, INACTIVE, GRADUATED
    }

    public enum OrderType {
        LOCAL, INTERNATIONAL, LEGAL_DELEGATE
    }
}
