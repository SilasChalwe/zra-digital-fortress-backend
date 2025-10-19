package zm.zra.zra_digital_fortress_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zm.zra.zra_digital_fortress_backend.dto.response.ApiResponse;
import zm.zra.zra_digital_fortress_backend.model.AuditLog;
import zm.zra.zra_digital_fortress_backend.model.User;
import zm.zra.zra_digital_fortress_backend.repository.AuditLogRepository;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Audit Logs", description = "Audit log APIs")
public class AuditController {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @GetMapping("/my-activities")
    @Operation(summary = "Get current user's activity logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getMyActivities(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AuditLog> logs = auditLogRepository.findByUserIdOrderByTimestampDesc(user.getId());

        return ResponseEntity.ok(ApiResponse.<List<AuditLog>>builder()
                .success(true)
                .message("Activity logs retrieved successfully")
                .data(logs)
                .build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ZRA_STAFF')")
    @Operation(summary = "Get user activity logs (Admin/Staff only)")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getUserActivities(
            @PathVariable String userId) {

        List<AuditLog> logs = auditLogRepository.findByUserIdOrderByTimestampDesc(userId);

        return ResponseEntity.ok(ApiResponse.<List<AuditLog>>builder()
                .success(true)
                .message("User activity logs retrieved successfully")
                .data(logs)
                .build());
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ZRA_STAFF')")
    @Operation(summary = "Get logs for specific entity (Admin/Staff only)")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getEntityLogs(
            @PathVariable String entityType,
            @PathVariable String entityId) {

        AuditLog.EntityType type = AuditLog.EntityType.valueOf(entityType);
        List<AuditLog> logs = auditLogRepository.findByEntityTypeAndEntityId(type, entityId);

        return ResponseEntity.ok(ApiResponse.<List<AuditLog>>builder()
                .success(true)
                .message("Entity logs retrieved successfully")
                .data(logs)
                .build());
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'ZRA_STAFF')")
    @Operation(summary = "Get logs within date range (Admin/Staff only)")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<AuditLog> logs = auditLogRepository.findByTimestampBetween(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.<List<AuditLog>>builder()
                .success(true)
                .message("Audit logs retrieved successfully")
                .data(logs)
                .build());
    }
}