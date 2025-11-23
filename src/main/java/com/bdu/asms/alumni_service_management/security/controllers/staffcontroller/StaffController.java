package com.bdu.asms.alumni_service_management.security.controllers.staffcontroller;


import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffRegistrationResponse;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.StaffUpdateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.UserRegistrationRequestDTO;
import com.bdu.asms.alumni_service_management.security.services.staffservice.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "Staff", description = "Staff management APIs")
//@SecurityRequirement(name = "bearerAuth")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Create staff",
            description = "Creates a staff record linked to a user and organizational unit",
            operationId = "createStaff"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedStaff",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "CREATED",
                                              "message": "Resource created",
                                              "data": {
                                                "publicId": "STF-xyz123",
                                                "userPublicId": "USR-abc123",
                                                "organizationalUnitPublicId": "OU-001",
                                                "title": "Lecturer"
                                              },
                                              "timestamp": "2025-10-20T12:46:54",
                                              "path": "/api/staff"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate staff for user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> create(
            @RequestBody(
                    description = "Payload to create a staff entry",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRegistrationRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody UserRegistrationRequestDTO dto
    ) {
        StaffRegistrationResponse created = staffService.registerStaff(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.getPublicId())
                .toUri();
        return ResponseFactory.created(location, created);
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Update staff",
            description = "Updates an existing staff record by publicId",
            operationId = "updateStaff"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Staff not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> update(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Staff public identifier", required = true)
            @PathVariable String publicId,
            @RequestBody(description = "Fields to update", required = true,
                    content = @Content(schema = @Schema(implementation = StaffUpdateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody StaffUpdateDTO dto
    ) {
        StaffResponseDTO updated = staffService.updateStaff(publicId, dto);
        return ResponseFactory.ok(updated);
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Get staff by publicId",
            description = "Returns a single staff record",
            operationId = "getStaff"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Staff not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getOne(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Staff public identifier", required = true)
            @PathVariable String publicId
    ) {
        StaffResponseDTO staff = staffService.getStaffByPublicId(publicId);
        return ResponseFactory.ok(staff);
    }

    @GetMapping
   @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List staff (paged)",
            description = "Returns a paginated list of staff",
            operationId = "listStaff"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> list(
            @ParameterObject Pageable pageable
    ) {
        Page<StaffResponseDTO> page = staffService.getStaff(pageable);
        return ResponseFactory.paged(page);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List all staff",
            description = "Returns all staff (non-paginated). For production, prefer the paginated endpoint.",
            operationId = "listAllStaff"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> listAll() {
        List<StaffResponseDTO> staff = staffService.getAllStaff();
        return ResponseFactory.list(staff);
    }

    @GetMapping("/by-user/{userPublicId}")
   @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Get staff by user publicId",
            description = "Fetches the staff record associated with the given user publicId",
            operationId = "getStaffByUserPublicId"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getByUser(
            @Parameter(name = "userPublicId", in = ParameterIn.PATH, description = "User public identifier", required = true)
            @PathVariable String userPublicId
    ) {
        StaffResponseDTO staff = staffService.getStaffByUserPublicId(userPublicId);
        return ResponseFactory.ok(staff);
    }

    @GetMapping("/by-org-unit/{orgUnitPublicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List staff by organizational unit",
            description = "Returns staff members belonging to the given organizational unit",
            operationId = "getStaffByOrganizationalUnit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getByOrgUnit(
            @Parameter(name = "orgUnitPublicId", in = ParameterIn.PATH, description = "Organizational Unit public identifier", required = true)
            @PathVariable String orgUnitPublicId
    ) {
        List<StaffResponseDTO> staff = staffService.getStaffByOrganizationalUnit(orgUnitPublicId);
        return ResponseFactory.list(staff);
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Delete staff",
            description = "Deletes a staff record after validating business constraints",
            operationId = "deleteStaff"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Staff not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> delete(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Staff public identifier", required = true)
            @PathVariable String publicId
    ) {
        staffService.deleteStaff(publicId);
        return ResponseFactory.noContent();
    }

    @GetMapping("/by-unit-only/{unitPublicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List staff by unit (paged, direct unit only)",
            description = "Returns a paginated list of staff for the given organizational unit (without children)",
            operationId = "listStaffByUnitOnly"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> listByUnitOnly(
            @PathVariable String unitPublicId,
            @ParameterObject Pageable pageable
    ) {
        Page<StaffResponseDTO> page = staffService.getStaffByUnit(unitPublicId, pageable);
        return ResponseFactory.paged(page);
    }


}