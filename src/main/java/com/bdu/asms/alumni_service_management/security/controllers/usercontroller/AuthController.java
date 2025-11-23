package com.bdu.asms.alumni_service_management.security.controllers.usercontroller;

import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.AuthRequestDTO;
import com.bdu.asms.alumni_service_management.security.services.userservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate and get JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseFactory.ok(authService.login(request));
    }


}
