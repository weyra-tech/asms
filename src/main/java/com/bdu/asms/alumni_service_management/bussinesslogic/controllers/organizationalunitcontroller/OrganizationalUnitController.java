package com.bdu.asms.alumni_service_management.bussinesslogic.controllers.organizationalunitcontroller;


import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitCreateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitHierarchyDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitResponseDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.dtos.organizationalunitdtos.OrganizationalUnitUpdateDTO;
import com.bdu.asms.alumni_service_management.bussinesslogic.services.organizationalunitservice.services.OrganizationalUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/public/api/org-units")
@RequiredArgsConstructor
@Tag(name = "Organizational Units", description = "Organizational structure management APIs")
//@SecurityRequirement(name = "bearerAuth")
public class OrganizationalUnitController {

    private final OrganizationalUnitService organizationalUnitService;

    @PostMapping
    //@PreAuthorize("hasAuthority('CREATE_ORG_UNIT')")
    @Operation(
            summary = "Create an organizational unit",
            description = "Creates a new organizational unit. Name, abbreviation, and unitType are required.",
            operationId = "createOrganizationalUnit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedOrgUnit",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "CREATED",
                                              "message": "Resource created",
                                              "data": {
                                                "publicId": "ORG-001",
                                                "name": "Faculty of Engineering",
                                                "abbreviation": "FOE",
                                                "unitType": "FACULTY",
                                                "parentPublicId": "ORG-UNIV"
                                              },
                                              "timestamp": "2025-10-20T13:35:00",
                                              "path": "/api/org-units"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> create(
            @RequestBody(
                    required = true,
                    description = "Payload to create an organizational unit",
                    content = @Content(schema = @Schema(implementation = OrganizationalUnitCreateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody OrganizationalUnitCreateDTO dto
    ) {
        OrganizationalUnitResponseDTO created = organizationalUnitService.createOrganizationalUnit(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.getPublicId())
                .toUri();
        return ResponseFactory.created(location, created);
    }

    @PostMapping("/root")
    //@PreAuthorize("hasAuthority('CREATE_ORG_UNIT')")
    @Operation(
            summary = "Create root organizational unit",
            description = "Creates the root unit (University). Only one root is allowed.",
            operationId = "createRootOrganizationalUnit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Root already exists or invalid payload",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> createRoot(
            @RequestBody(
                    required = true,
                    description = "Payload to create the root unit (type will be set to UNIVERSITY)",
                    content = @Content(schema = @Schema(implementation = OrganizationalUnitCreateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody OrganizationalUnitCreateDTO dto
    ) {
        OrganizationalUnitResponseDTO created = organizationalUnitService.createRootOrganizationalUnit(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.getPublicId())
                .toUri();
        return ResponseFactory.created(location, created);
    }

    @PutMapping("/{publicId}")
    //@PreAuthorize("hasAuthority('UPDATE_ORG_UNIT')")
    @Operation(
            summary = "Update an organizational unit",
            description = "Partially updates an organizational unit. Parent can be changed via parentPublicId.",
            operationId = "updateOrganizationalUnit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> update(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Unit public identifier", required = true)
            @PathVariable String publicId,
            @RequestBody(description = "Fields to update", required = true,
                    content = @Content(schema = @Schema(implementation = OrganizationalUnitUpdateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody OrganizationalUnitUpdateDTO dto
    ) {
        OrganizationalUnitResponseDTO updated = organizationalUnitService.updateOrganizationalUnit(publicId, dto);
        return ResponseFactory.ok(updated);
    }

    @GetMapping("/{publicId}")
    //@PreAuthorize("hasAuthority('READ_ORG_UNIT')")
    @Operation(
            summary = "Get an organizational unit by publicId",
            description = "Returns a single organizational unit",
            operationId = "getOrganizationalUnit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getOne(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Unit public identifier", required = true)
            @PathVariable String publicId
    ) {
        OrganizationalUnitResponseDTO unit = organizationalUnitService.getOrganizationalUnitByPublicId(publicId);
        return ResponseFactory.ok(unit);
    }

    @GetMapping("/{publicId}/hierarchy")
   // @PreAuthorize("hasAuthority('READ_ORG_UNIT')")
    @Operation(
            summary = "Get organizational hierarchy",
            description = "Returns the hierarchical tree starting from the specified unit",
            operationId = "getOrganizationalHierarchy"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getHierarchy(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Unit public identifier", required = true)
            @PathVariable String publicId
    ) {
        OrganizationalUnitHierarchyDTO hierarchy = organizationalUnitService.getOrganizationalUnitHierarchy(publicId);
        return ResponseFactory.ok(hierarchy);
    }

    @GetMapping("/{publicId}/children")
    //@PreAuthorize("hasAuthority('READ_ORG_UNIT')")
    @Operation(
            summary = "List child units",
            description = "Returns immediate children of a unit",
            operationId = "getChildUnits"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getChildren(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Parent unit public identifier", required = true)
            @PathVariable String publicId
    ) {
        List<OrganizationalUnitResponseDTO> children = organizationalUnitService.getChildUnits(publicId);
        return ResponseFactory.list(children);
    }

    @GetMapping
    //@PreAuthorize("hasAuthority('READ_ORG_UNIT')")
    @Operation(
            summary = "Search/list organizational units (paged)",
            description = "If a keyword is provided, performs a case-insensitive search by name, abbreviation, or email; otherwise returns all units paged.",
            operationId = "searchOrganizationalUnits"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> listOrSearch(
            @Parameter(description = "Search keyword for name/abbreviation/email")
            @RequestParam(value = "keyword", required = false) String keyword,
            @ParameterObject Pageable pageable
    ) {
        Page<OrganizationalUnitResponseDTO> page = organizationalUnitService.searchUnits(keyword, pageable);
        return ResponseFactory.paged(page);
    }


    @GetMapping("/all")
   // @PreAuthorize("hasAuthority('READ_ORG_UNIT')")
    @Operation(
            summary = "List all units (non-paged)",
            description = "Returns all organizational units. Prefer the paged search endpoint in production.",
            operationId = "listAllOrganizationalUnits"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> listAll() {
        List<OrganizationalUnitResponseDTO> units = organizationalUnitService.getAllOrganizationalUnits();
        return ResponseFactory.list(units);
    }

    @DeleteMapping("/{publicId}")
    //@PreAuthorize("hasAuthority('DELETE_ORG_UNIT')")
    @Operation(
            summary = "Delete an organizational unit",
            description = "Deletes a unit if it has no child units (and optionally no appointments)",
            operationId = "deleteOrganizationalUnit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Unit has child units or appointments",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unit not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> delete(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Unit public identifier", required = true)
            @PathVariable String publicId
    ) {
        organizationalUnitService.deleteOrganizationalUnit(publicId);
        return ResponseFactory.noContent();
    }


}