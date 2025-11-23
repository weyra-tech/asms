package com.bdu.asms.alumni_service_management.security.entities;


import com.bdu.asms.alumni_service_management.security.enums.PositionLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "POSITION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PUBLIC_ID", unique = true, nullable = false)
    private String publicId;

    @Column(name = "TITLE", nullable = false, unique = true)
    private String name; // e.g., "Dean", "Department Head", "Coordinator"

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "LEVEL")
    private PositionLevel level; // e.g., UNIVERSITY, FACULTY, DEPARTMENT

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

