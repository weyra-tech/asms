package com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure;

import com.bdu.asms.alumni_service_management.security.entities.Appointment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an organizational unit within the university structure —
 * e.g., University, Faculty, Institute, Department, Center, etc.
 *
 * Each unit can have:
 *  - a parent unit (for hierarchy)
 *  - child units (for substructure)
 *  - multiple appointments (positions assigned to staff)
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "ORGANIZATIONAL_UNIT")
@Builder
public class OrganizationalUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PUBLIC_ID", unique = true, nullable = false, updatable = false)
    private String publicId; // UUID or external identifier

    @Column(name = "UNIT_NAME", unique = true, nullable = false)
    private String name;

    @Column(name = "ABBREVIATION", unique = true, nullable = false)
    private String abbreviation;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UNIT_TYPE_ID", nullable = false)
    private OrganizationalUnitType unitType;
    // e.g., UNIVERSITY, FACULTY, DEPARTMENT, CENTER, DIRECTORATE

    // ==========================
    // 🔁 Hierarchical Structure
    // ==========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private OrganizationalUnit parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationalUnit> children = new ArrayList<>();

    // ==========================
    // 📞 Contact Information
    // ==========================

    @Column(name = "UNIT_EMAIL", unique = true)
    private String unitEmail;

    @Column(name = "PHONE_NUMBER")
    @Pattern(
            regexp = "^\\+251\\d{9}$",
            message = "Invalid phone number. Must start with +251 followed by 9 digits."
    )
    private String phoneNumber;

    // ==========================
    // 👥 Appointments & Relations
    // ==========================

    /**
     * List of all active and historical appointments
     * (Heads, Coordinators, Instructors, Directors, etc.)
     */
    @OneToMany(mappedBy = "organizationalUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Appointment> appointments = new ArrayList<>();

    // ==========================
    // ⚙️ Utility Methods
    // ==========================

    public void addChild(OrganizationalUnit child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(OrganizationalUnit child) {
        children.remove(child);
        child.setParent(null);
    }

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

