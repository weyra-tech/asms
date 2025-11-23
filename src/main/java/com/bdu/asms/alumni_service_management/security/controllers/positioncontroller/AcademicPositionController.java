package com.bdu.asms.alumni_service_management.security.controllers.positioncontroller;



import com.bdu.asms.alumni_service_management.security.entities.AcademicPosition;
import com.bdu.asms.alumni_service_management.security.services.academicpositionservice.services.AcademicPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/api/academic-positions")
@RequiredArgsConstructor
@Tag(name = "Academic Position Management", description = "APIs for managing academic positions")
public class AcademicPositionController {

    private final AcademicPositionService academicPositionService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Create a new academic position")
    public ResponseEntity<AcademicPosition> create(@RequestBody AcademicPosition position) {
        return ResponseEntity.ok(academicPositionService.createPosition(position));
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Update an existing academic position")
    public ResponseEntity<AcademicPosition> update(@PathVariable String publicId,
                                                   @RequestBody AcademicPosition updatedPosition) {
        return ResponseEntity.ok(academicPositionService.updatePosition(publicId, updatedPosition));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Get all academic positions")
    public ResponseEntity<List<AcademicPosition>> getAll() {
        return ResponseEntity.ok(academicPositionService.getAllPositions());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Get all active academic positions")
    public ResponseEntity<List<AcademicPosition>> getActive() {
        return ResponseEntity.ok(academicPositionService.getActivePositions());
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Get academic position by publicId")
    public ResponseEntity<AcademicPosition> getByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(academicPositionService.getPositionByPublicId(publicId));
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Delete academic position by publicId")
    public ResponseEntity<Void> delete(@PathVariable String publicId) {
        academicPositionService.deletePosition(publicId);
        return ResponseEntity.noContent().build();
    }
}

