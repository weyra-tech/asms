package com.bdu.asms.alumni_service_management.bussinesslogic.dtos.servicesdtos.category;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceCategoryCreateDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Base fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Fee must be non-negative")
    private BigDecimal baseFee;
}
