package com.bdu.asms.alumni_service_management.security.controllers.positioncontroller;

import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentCreateDTO;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentResponseDTO;
import com.bdu.asms.alumni_service_management.security.dtos.appointmentdtos.AppointmentUpdateDTO;
import com.bdu.asms.alumni_service_management.security.services.positionservice.services.AppointmentService;

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
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Appointment management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Create an appointment",
            description = "Creates a new appointment for a staff member in an organizational unit and position",
            operationId = "createAppointment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedAppointment",
                                    value = """
                                            {
                                              "success": true,
                                              "code": "CREATED",
                                              "message": "Resource created",
                                              "data": {
                                                "publicId": "APT-001",
                                                "staffPublicId": "STF-123",
                                                "positionPublicId": "POS-001",
                                                "organizationalUnitPublicId": "OU-001",
                                                "status": "ACTIVE",
                                                "startDate": "2025-10-20",
                                                "endDate": null
                                              },
                                              "timestamp": "2025-10-20T13:20:00",
                                              "path": "/api/appointments"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Related entity not found (staff/position/org unit)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> create(
            @RequestBody(
                    required = true,
                    description = "Payload to create a new appointment",
                    content = @Content(schema = @Schema(implementation = AppointmentCreateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody AppointmentCreateDTO dto
    ) {
        AppointmentResponseDTO created = appointmentService.createAppointment(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.getPublicId())
                .toUri();
        return ResponseFactory.created(location, created);
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Update an appointment",
            description = "Updates an existing appointment by publicId. Start date can only be changed when status is PENDING.",
            operationId = "updateAppointment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid status transition",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> update(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Appointment public identifier", required = true)
            @PathVariable String publicId,
            @RequestBody(description = "Fields to update", required = true,
                    content = @Content(schema = @Schema(implementation = AppointmentUpdateDTO.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody AppointmentUpdateDTO dto
    ) {
        AppointmentResponseDTO updated = appointmentService.updateAppointment(publicId, dto);
        return ResponseFactory.ok(updated);
    }

    @PostMapping("/{publicId}/end")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "End an appointment",
            description = "Terminates an active appointment. Sets endDate to today and status to TERMINATED.",
            operationId = "endAppointment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ended",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Appointment already ended or invalid timing",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> end(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Appointment public identifier", required = true)
            @PathVariable String publicId
    ) {
        appointmentService.endAppointment(publicId);
        // Return 200 OK with no body data in the envelope, or 204 No Content if preferred
        return ResponseFactory.ok(null);
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "Get an appointment by publicId",
            description = "Returns a single appointment by publicId",
            operationId = "getAppointment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getOne(
            @Parameter(name = "publicId", in = ParameterIn.PATH, description = "Appointment public identifier", required = true)
            @PathVariable String publicId
    ) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(publicId);
        return ResponseFactory.ok(appointment);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List appointments (paged)",
            description = "Returns a paginated list of appointments",
            operationId = "listAppointments"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> list(
            @ParameterObject Pageable pageable
    ) {
        Page<AppointmentResponseDTO> page = appointmentService.getAppointments(pageable);
        return ResponseFactory.paged(page);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List all appointments",
            description = "Returns all appointments (non-paginated). For production, prefer the paginated endpoint.",
            operationId = "listAllAppointments"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> listAll() {
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return ResponseFactory.list(appointments);
    }

    @GetMapping("/by-staff/{staffPublicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List appointments by staff",
            description = "Returns all appointments for the given staff publicId",
            operationId = "getAppointmentsByStaff"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Staff not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getByStaff(
            @Parameter(name = "staffPublicId", in = ParameterIn.PATH, description = "Staff public identifier", required = true)
            @PathVariable String staffPublicId
    ) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByStaff(staffPublicId);
        return ResponseFactory.list(appointments);
    }

    @GetMapping("/by-org-unit/{orgUnitPublicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(
            summary = "List appointments by organizational unit",
            description = "Returns all appointments belonging to the given organizational unit",
            operationId = "getAppointmentsByOrganizationalUnit"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation =com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Organizational unit not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bdu.asms.alumni_service_management.bussinesslogic.api.ApiResponse.class)))
    })
    public ResponseEntity<?> getByOrgUnit(
            @Parameter(name = "orgUnitPublicId", in = ParameterIn.PATH, description = "Organizational Unit public identifier", required = true)
            @PathVariable String orgUnitPublicId
    ) {
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByOrganizationalUnit(orgUnitPublicId);
        return ResponseFactory.list(appointments);
    }
}