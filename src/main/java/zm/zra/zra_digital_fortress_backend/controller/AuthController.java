package zm.zra.zra_digital_fortress_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zm.zra.zra_digital_fortress_backend.dto.request.LoginRequest;
import zm.zra.zra_digital_fortress_backend.dto.request.RegisterBusinessRequest;
import zm.zra.zra_digital_fortress_backend.dto.request.RegisterIndividualRequest;
import zm.zra.zra_digital_fortress_backend.dto.response.ApiResponse;
import zm.zra.zra_digital_fortress_backend.dto.response.AuthResponse;
import zm.zra.zra_digital_fortress_backend.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and Registration APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/individual")
    @Operation(summary = "Register as individual taxpayer")
    public ResponseEntity<ApiResponse<AuthResponse>> registerIndividual(
            @Valid @RequestBody RegisterIndividualRequest request) {
        
        AuthResponse response = authService.registerIndividual(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Registration successful. Please verify your email to activate your account.")
                        .data(response)
                        .build());
    }

    @PostMapping("/register/business")
    @Operation(summary = "Register as business taxpayer")
    public ResponseEntity<ApiResponse<AuthResponse>> registerBusiness(
            @Valid @RequestBody RegisterBusinessRequest request) {
        
        AuthResponse response = authService.registerBusiness(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Registration successful. Please verify your email to activate your account.")
                        .data(response)
                        .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login to the system")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIpAddress(httpRequest);
        AuthResponse response = authService.login(request, ipAddress);
        
        String message = response.getMfaRequired() 
            ? "MFA verification required" 
            : "Login successful";
        
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message(message)
                .data(response)
                .build());
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmail(
            @RequestParam String token) {
        
        AuthResponse response = authService.verifyEmail(token);
        
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Email verified successfully. Your account is now active.")
                .data(response)
                .build());
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestParam String email) {
        
        String resetToken = authService.generatePasswordResetToken(email);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Password reset instructions have been sent to your email.")
                .data(resetToken)
                .build());
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestParam String refreshToken) {
        
        AuthResponse response = authService.refreshToken(refreshToken);
        
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Token refreshed successfully")
                .data(response)
                .build());
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}