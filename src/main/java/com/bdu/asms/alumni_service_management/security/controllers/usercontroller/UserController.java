package com.bdu.asms.alumni_service_management.security.controllers.usercontroller;



import com.bdu.asms.alumni_service_management.bussinesslogic.api.ResponseFactory;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.AdminPasswordResetRequestDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.PasswordChangeRequestDTO;
import com.bdu.asms.alumni_service_management.security.dtos.userdtos.UserStatusUpdateRequestDTO;
import com.bdu.asms.alumni_service_management.security.services.userservice.service.AuthService;
import com.bdu.asms.alumni_service_management.security.services.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
//@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserController.class);


    @GetMapping("/me")
    @Operation(summary = "Get current user's profile and context")
    public ResponseEntity<?> me() {
        return ResponseFactory.ok(authService.getProfile());
    }

    // 🔒 1️⃣ Change Password (Self)
    @PostMapping(value = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
// @PreAuthorize("hasAuthority('CHANGE_PASSWORD')")
    @Operation(
            summary = "Change password (self)",
            description = "Allows a logged-in user to change their own password",
            operationId = "changePassword"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequestDTO request) {
        log.debug("ChangePassword payload: oldPassword null? {}, newPassword null? {}",
                request.getOldPassword() == null, request.getNewPassword() == null);
        userService.changePassword(request);
        return ResponseFactory.ok("Password changed successfully");
    }

    // 🧑‍💼 2️⃣ Reset Password (By Admin)
    @PutMapping("/{userPublicId}/reset-password")
   // @PreAuthorize("hasAuthority('RESET_PASSWORD_BY_ADMIN')")
    @Operation(
            summary = "Reset password (admin)",
            description = "Allows an administrator to reset a user’s password",
            operationId = "resetPasswordByAdmin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> resetPasswordByAdmin(
            @PathVariable String userPublicId,
            @RequestBody AdminPasswordResetRequestDTO request
    ) {
        userService.resetPasswordByAdmin(userPublicId, request);
        return ResponseFactory.ok("Password reset successfully by admin");
    }

    // 🚦 3️⃣ Enable / Disable User (By Admin)
    @PutMapping("/{publicId}/status")
   // @PreAuthorize("hasAuthority('UPDATE_USER_STATUS')")
    @Operation(
            summary = "Enable or disable user (admin)",
            description = "Allows an administrator to enable, disable, or lock a user account",
            operationId = "updateUserStatus"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> updateUserStatus(
            @PathVariable String publicId,
            @RequestBody UserStatusUpdateRequestDTO request
    ) {
        userService.updateUserStatus(publicId, request);
        return ResponseFactory.ok("User status updated successfully");
    }
}