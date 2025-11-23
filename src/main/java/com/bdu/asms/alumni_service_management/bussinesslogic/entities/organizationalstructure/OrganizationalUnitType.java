package com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORGANIZATIONAL_UNIT_TYPE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrganizationalUnitType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "PUBLIC_ID", unique = true, nullable = false)
    private String publicId;
    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

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
