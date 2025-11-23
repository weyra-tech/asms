package com.bdu.asms.alumni_service_management.security.repository.staffrepository;

import com.bdu.asms.alumni_service_management.security.entities.Staff;


public interface StaffResourceCountProjection {
    Staff getStaff();
    long getResourceCount();
}
