package com.bdu.asms.alumni_service_management.bussinesslogic.services.organizationalunitservice.services;


import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnitType;

import java.util.List;

public interface OrganizationalUnitTypeService {
    OrganizationalUnitType create(OrganizationalUnitType unitType);
    OrganizationalUnitType update(String publicId, OrganizationalUnitType updatedUnitType);
    void delete(String publicId);
    OrganizationalUnitType getByPublicId(String publicId);
    List<OrganizationalUnitType> getAll();

}

