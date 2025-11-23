package com.bdu.asms.alumni_service_management.security.controllers.rolecontrollers;

import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.roledtos.RoleUpdateDTO;
import com.bdu.asms.alumni_service_management.security.services.roleservices.services.RoleService;

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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management APIs")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Create a role",
            description = "Creates a new role with optional parent role and permissions",
            operationId = "createRole"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedRole",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "CREATED",
                                              "message": "Resource created",
                                              "data": {
                                                "publicId": "ROLE-001",
                                                "name": "DEPARTMENT_ADMIN",
                                                "description": "Department level admin",
                                                "parentRolePublicId": null,
                                                "permissionPublicIds": ["PERM-COURSE-CREATE","PERM-COURSE-UPDATE"]
                                              },
                                              "timestamp": "2025-10-20T13:00:00",
                                              "path": "/api/roles"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate role name",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> create(
            @RequestBody(
                    required = true,
                    description = "Payload to create a new role",
                    content = @Content(schema = @Schema(implementation = RoleCreateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody RoleCreateDTO dto
    ) {
        RoleResponseDTO created = roleService.createRole(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.getPublicId())
                .toUri();
        return ResponseFactory.created(location, created);
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Update a role",
            description = "Updates an existing role by publicId",
            operationId = "updateRole"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> update(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Role public identifier", required = true)
            @PathVariable String publicId,
            @RequestBody(description = "Fields to update", required = true,
                    content = @Content(schema = @Schema(implementation = RoleUpdateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody RoleUpdateDTO dto
    ) {
        RoleResponseDTO updated = roleService.updateRole(publicId, dto);
        return ResponseFactory.ok(updated);
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Get a role by publicId",
            description = "Returns a single role by publicId",
            operationId = "getRole"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getOne(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Role public identifier", required = true)
            @PathVariable String publicId
    ) {
        RoleResponseDTO role = roleService.getRoleByPublicId(publicId);
        return ResponseFactory.ok(role);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List roles (paged)",
            description = "Returns a paginated list of roles",
            operationId = "listRoles"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> list(
            @ParameterObject Pageable pageable
    ) {
        Page<RoleResponseDTO> page = roleService.getRoles(pageable);
        return ResponseFactory.paged(page);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List all roles",
            description = "Returns all roles (non-paginated). For production, prefer the paginated endpoint.",
            operationId = "listAllRoles"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> listAll() {
        List<RoleResponseDTO> roles = roleService.getAllRoles();
        return ResponseFactory.list(roles);
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Delete a role",
            description = "Deletes a role if not assigned to users and has no child roles",
            operationId = "deleteRole"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Role assigned to users or has child roles",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> delete(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Role public identifier", required = true)
            @PathVariable String publicId
    ) {
        roleService.deleteRole(publicId);
        return ResponseFactory.noContent();
    }
}