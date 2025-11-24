package com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_category;


import com.bdu.asms.alumni_service_management.bussinesslogic.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "service_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategory extends BaseEntity {



    @Column(nullable = false, unique = true)
    private String name; // e.g., "Official Transcript", "Original Degree"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal baseFee;

    @Column(name = "is_active")
    private boolean isActive = true;
}
