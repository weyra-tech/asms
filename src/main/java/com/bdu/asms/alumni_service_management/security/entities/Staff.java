package com.bdu.asms.alumni_service_management.security.entities;


import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import com.bdu.asms.alumni_service_management.security.enums.GenderEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "STAFF")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PUBLIC_ID")
    private String publicId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;

    @Column(name = "FIRST_NAME")
    private String fName;

    @Column(name = "MIDDLE_NAME")
    private String mName;

    @Column(name = "LAST_NAME")
    private String lName;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER")
    private GenderEnum gender;

    @Column(name = "NATIONALITY")
    private String nationality;

    @Column(name = "PHONE_NUMBER")
    @Pattern(regexp = "^\\+251\\d{9}$", message = "Invalid phone number. It must start with +251 followed by 9 digits.")
    private String phoneNumber;

    @Column(name = "EMPLOYMENT_ID")
    private String employmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACADEMIC_POSITION_ID")
    private AcademicPosition academicPosition;

    @Column(name = "PROFILE_SUMMARY")
    private String profileSummary;

    @Column(name = "THUMBNAIL")
    private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATIONAL_UNIT_ID", nullable = false)
    private OrganizationalUnit organizationalUnit;

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

    public String getFullName() {
        return String.format("%s %s %s", fName, mName, lName);
    }
}
