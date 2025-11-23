package com.bdu.asms.alumni_service_management.security.controllers.rolecontrollers;


import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.permissiondtos.PermissionUpdateDTO;
import com.bdu.asms.alumni_service_management.security.services.roleservices.services.PermissionService;
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

import java.util.List;


@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Permission management APIs")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Create a permission",
            description = "Creates a new permission",
            operationId = "createPermission"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedPermission",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "CREATED",
                                              "message": "Resource created",
                                              "data": {
                                                "publicId": "PERM-ABC123",
                                                "name": "COURSE_CREATE"
                                              },
                                              "timestamp": "2025-10-20T13:05:00",
                                              "path": "/api/permissions"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate permission name",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> create(
            @RequestBody(
                    required = true,
                    description = "Payload to create a new permission",
                    content = @Content(schema = @Schema(implementation = PermissionCreateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody PermissionCreateDTO dto
    ) {
        PermissionResponseDTO created = permissionService.createPermission(dto);
        // 201 Created without Location is fine here; if you want Location, build it similarly to other controllers
        return ResponseFactory.created(null, created);
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Update a permission",
            description = "Updates an existing permission by publicId",
            operationId = "updatePermission"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Permission not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> update(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Permission public identifier", required = true)
            @PathVariable String publicId,
            @RequestBody(description = "Fields to update", required = true,
                    content = @Content(schema = @Schema(implementation = PermissionUpdateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody PermissionUpdateDTO dto
    ) {
        PermissionResponseDTO updated = permissionService.updatePermission(publicId, dto);
        return ResponseFactory.ok(updated);
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Get a permission by publicId",
            description = "Returns a single permission by publicId",
            operationId = "getPermission"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Permission not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getOne(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Permission public identifier", required = true)
            @PathVariable String publicId
    ) {
        PermissionResponseDTO permission = permissionService.getPermissionByPublicId(publicId);
        return ResponseFactory.ok(permission);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List permissions (paged)",
            description = "Returns a paginated list of permissions",
            operationId = "listPermissions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> list(
            @ParameterObject Pageable pageable
    ) {
        Page<PermissionResponseDTO> page = permissionService.getPermissions(pageable);
        return ResponseFactory.paged(page);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List all permissions",
            description = "Returns all permissions (non-paginated). For production, prefer the paginated endpoint.",
            operationId = "listAllPermissions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> listAll() {
        List<PermissionResponseDTO> permissions = permissionService.getAllPermissions();
        return ResponseFactory.list(permissions);
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Delete a permission",
            description = "Deletes a permission if it is not assigned to any role",
            operationId = "deletePermission"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Permission assigned to roles",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Permission not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> delete(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Permission public identifier", required = true)
            @PathVariable String publicId
    ) {
        permissionService.deletePermission(publicId);
        return ResponseFactory.noContent();
    }
}