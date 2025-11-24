package com.bdu.asms.alumni_service_management.bussinesslogic.dtos.servicesdtos.category;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceCategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal baseFee;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
