package com.bdu.asms.alumni_service_management.bussinesslogic.dtos.servicesdtos.category;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceCategoryUpdateDTO {
    private String name;
    private String description;
    private BigDecimal baseFee;
    private Boolean isActive;
}
