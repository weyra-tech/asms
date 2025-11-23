package com.bdu.asms.alumni_service_management.security.controllers.positioncontroller;


import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.positiondtos.PositionUpdateDTO;
import com.bdu.asms.alumni_service_management.security.services.positionservice.services.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/positions")
@RequiredArgsConstructor
@Tag(name = "Positions", description = "Position management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Create a position",
            description = "Creates a new position",
            operationId = "createPosition"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedPosition",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "CREATED",
                                              "message": "Resource created",
                                              "data": {
                                                "publicId": "POS-001",
                                                "title": "Senior Lecturer",
                                                "description": "Academic staff position",
                                                "level": 3,
                                                "academic": true,
                                                "active": true
                                              },
                                              "timestamp": "2025-10-20T13:10:00",
                                              "path": "/api/positions"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate title",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> create(
            @RequestBody(
                    required = true,
                    description = "Payload to create a new position",
                    content = @Content(schema = @Schema(implementation = PositionCreateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody PositionCreateDTO dto
    ) {
        PositionResponseDTO created = positionService.createPosition(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.getPublicId())
                .toUri();
        return ResponseFactory.created(location, created);
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Update a position",
            description = "Updates an existing position by publicId",
            operationId = "updatePosition"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Position not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> update(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Position public identifier", required = true)
            @PathVariable String publicId,
            @RequestBody(description = "Fields to update", required = true,
                    content = @Content(schema = @Schema(implementation = PositionUpdateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody PositionUpdateDTO dto
    ) {
        PositionResponseDTO updated = positionService.updatePosition(publicId, dto);
        return ResponseFactory.ok(updated);
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Get a position by publicId",
            description = "Returns a single position by publicId",
            operationId = "getPosition"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Position not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getOne(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Position public identifier", required = true)
            @PathVariable String publicId
    ) {
        PositionResponseDTO position = positionService.getPositionByPublicId(publicId);
        return ResponseFactory.ok(position);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List positions (paged)",
            description = "Returns a paginated list of positions",
            operationId = "listPositions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> list(
            @ParameterObject Pageable pageable
    ) {
        Page<PositionResponseDTO> page = positionService.getPositions(pageable);
        return ResponseFactory.paged(page);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List all positions",
            description = "Returns all positions (non-paginated). For production, prefer the paginated endpoint.",
            operationId = "listAllPositions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> listAll() {
        List<PositionResponseDTO> positions = positionService.getAllPositions();
        return ResponseFactory.list(positions);
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Delete a position",
            description = "Deletes a position by publicId",
            operationId = "deletePosition"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Position not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> delete(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Position public identifier", required = true)
            @PathVariable String publicId
    ) {
        positionService.deletePosition(publicId);
        return ResponseFactory.noContent();
    }
}