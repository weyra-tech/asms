package com.bdu.asms.alumni_service_management.bussinesslogic.services.organizationalunitservice.services;


import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitCreateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitHierarchyDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitResponseDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitUpdateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface OrganizationalUnitService {

    /**
     * Creates a new organizational unit
     *
     * @param createDTO DTO containing creation details
     * @return Created organizational unit with generated IDs
     */
    OrganizationalUnitResponseDTO createOrganizationalUnit(OrganizationalUnitCreateDTO createDTO);

    /**
     * Retrieves organizational unit by public ID
     *
     * @param publicId Public identifier of the unit
     * @return Optional containing the unit if found
     */
    OrganizationalUnitResponseDTO getOrganizationalUnitByPublicId(String publicId);

    /**
     * Retrieves complete organizational hierarchy
     *
     * @param publicId Public identifier of the root unit
     * @return Hierarchy structure
     */
    OrganizationalUnitHierarchyDTO getOrganizationalUnitHierarchy(String publicId);

    /**
     * Updates an existing organizational unit
     *
     * @param publicId  Public identifier of the unit to update
     * @param updateDTO DTO containing updated fields
     * @return Updated organizational unit
     */
    OrganizationalUnitResponseDTO updateOrganizationalUnit(String publicId, OrganizationalUnitUpdateDTO updateDTO);

    /**
     * Deletes an organizational unit
     *
     * @param publicId Public identifier of the unit to delete
     */
    void deleteOrganizationalUnit(String publicId);

    /**
     * Retrieves all child units of a parent
     *
     * @param parentPublicId Public identifier of the parent unit
     * @return List of child units
     */
    List<OrganizationalUnitResponseDTO> getChildUnits(String parentPublicId);

    /**
     * Retrieves all organizational units
     *
     * @return Complete list of units
     */
    List<OrganizationalUnitResponseDTO> getAllOrganizationalUnits();

    /**
     * Retrieves units filtered by type
     *
   //  * @param unitType Type to filter by
     * @return List of matching units
     */
    //List<OrganizationalUnitResponseDTO> getOrganizationalUnitsByType(OrganizationalUnitType unitType);


    OrganizationalUnitResponseDTO createRootOrganizationalUnit(OrganizationalUnitCreateDTO createDTO);

    Page<OrganizationalUnitResponseDTO> searchUnits(String keyword, Pageable pageable);

    Set<OrganizationalUnit> getUnitAndChildren(String publicId);


}

