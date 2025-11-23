package com.bdu.asms.alumni_service_management.security.entities;

import com.bdu.asms.alumni_service_management.security.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "PUBLIC_ID")
    private String publicId;
    @Column(name = "USERNAME", unique = true, nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format.")
    private String userName; // login email

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_STATUS")
    private UserStatus userStatus = UserStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ROLE_USER",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @JsonIgnoreProperties("users")
    private Set<Role> roles = new HashSet<>();

    @Column(name = "IS_DELETED")
    private boolean deleted = false;

    @Column(name = "DELETED_ON")
    private Date deletedOn;


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

