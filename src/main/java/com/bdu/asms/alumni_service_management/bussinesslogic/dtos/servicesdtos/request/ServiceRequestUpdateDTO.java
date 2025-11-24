package com.bdu.asms.alumni_service_management.bussinesslogic.dtos.servicesdtos.request;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.service_request.ServiceRequest;
import lombok.Data;

@Data
public class ServiceRequestUpdateDTO {

    private ServiceRequest.RequestStatus status;
    private String rejectionReason;

    // Allow updating tracking number only if absolutely necessary (usually
    // auto-generated)
    // private String trackingNumber;
}
