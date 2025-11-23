package com.bdu.asms.alumni_service_management.security.entities;



import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import com.bdu.asms.alumni_service_management.security.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "APPOINTMENT")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "PUBLIC_ID", unique = true, nullable = false)
    private String publicId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STAFF_ID", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIT_ID", nullable = false)
    private OrganizationalUnit organizationalUnit;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "APPOINTMENT_STATUS")
    private AppointmentStatus appointmentStatus; // ACTIVE, EXPIRED, TERMINATED, PENDING


    @Column(name = "CREATE_USER_ID")
    private Long createUserId;


    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_USER_ID")
    private Long updateUserId;


    @Column(name = "UPDATE_TIME")
    private LocalDateTime  updateTime;

    @Column(name = "STATUS")
    private Integer status;  // e.g. 1 = active, 0 = disqualified
    @Lob
    @Column(name = "REMARKS")
    private String remarks;

}
