package com.bdu.asms.alumni_service_management.bussinesslogic.controllers.organizationalunitcontroller;

import com.bdu.asms.alumni_service_management.bussinesslogic.entities.organizationalstructure.OrganizationalUnitType;
import com.bdu.asms.alumni_service_management.bussinesslogic.services.organizationalunitservice.services.OrganizationalUnitTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/api/organizational-unit-types")
@RequiredArgsConstructor
@Tag(name = "Organizational Unit Type Management", description = "APIs for managing organizational unit types")
public class OrganizationalUnitTypeController {

    private final OrganizationalUnitTypeService service;

    @PostMapping
    @Operation(summary = "Create a new organizational unit type")
    public ResponseEntity<OrganizationalUnitType> create(@RequestBody OrganizationalUnitType unitType) {
        return ResponseEntity.ok(service.create(unitType));
    }

    @PutMapping("/{publicId}")
    @Operation(summary = "Update an existing organizational unit type")
    public ResponseEntity<OrganizationalUnitType> update(@PathVariable String publicId,
                                                         @RequestBody OrganizationalUnitType updatedUnitType) {
        return ResponseEntity.ok(service.update(publicId, updatedUnitType));
    }

    @GetMapping
    @Operation(summary = "Get all organizational unit types")
    public ResponseEntity<List<OrganizationalUnitType>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }



    @GetMapping("/{publicId}")
    @Operation(summary = "Get organizational unit type by publicId")
    public ResponseEntity<OrganizationalUnitType> getByPublicId(@PathVariable String publicId) {
        return ResponseEntity.ok(service.getByPublicId(publicId));
    }

    @DeleteMapping("/{publicId}")
    @Operation(summary = "Delete organizational unit type by publicId")
    public ResponseEntity<Void> delete(@PathVariable String publicId) {
        service.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}

