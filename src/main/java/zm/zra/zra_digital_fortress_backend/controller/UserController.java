package zm.zra.zra_digital_fortress_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zm.zra.zra_digital_fortress_backend.dto.response.ApiResponse;
import zm.zra.zra_digital_fortress_backend.dto.response.UserResponse;
import zm.zra.zra_digital_fortress_backend.model.Document;
import zm.zra.zra_digital_fortress_backend.model.User;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;
import zm.zra.zra_digital_fortress_backend.service.DocumentService;
import zm.zra.zra_digital_fortress_backend.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final DocumentService documentService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse profile = userService.getUserProfile(user.getId());

        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile retrieved successfully")
                .data(profile)
                .build());
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
            @Valid @RequestBody UserResponse updateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse updatedProfile = userService.updateUserProfile(user.getId(), updateRequest);

        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(updatedProfile)
                .build());
    }

    @PostMapping("/mfa/enable")
    @Operation(summary = "Enable MFA for user")
    public ResponseEntity<ApiResponse<Void>> enableMfa(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userService.enableMfa(user.getId());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("MFA enabled successfully")
                .build());
    }

    @PostMapping("/mfa/disable")
    @Operation(summary = "Disable MFA for user")
    public ResponseEntity<ApiResponse<Void>> disableMfa(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userService.disableMfa(user.getId());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("MFA disabled successfully")
                .build());
    }

    @PostMapping("/documents/upload")
    @Operation(summary = "Upload a document")
    public ResponseEntity<ApiResponse<Document>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = documentService.uploadDocument(
                user.getId(),
                file,
                Document.DocumentType.valueOf(documentType)
        );

        return ResponseEntity.ok(ApiResponse.<Document>builder()
                .success(true)
                .message("Document uploaded successfully")
                .data(document)
                .build());
    }

    @GetMapping("/documents/{documentId}")
    @Operation(summary = "Get document by ID")
    public ResponseEntity<ApiResponse<Document>> getDocument(
            @PathVariable String documentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = documentService.getDocument(documentId, user.getId());

        return ResponseEntity.ok(ApiResponse.<Document>builder()
                .success(true)
                .message("Document retrieved successfully")
                .data(document)
                .build());
    }

    @DeleteMapping("/documents/{documentId}")
    @Operation(summary = "Delete a document")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable String documentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        documentService.deleteDocument(documentId, user.getId());

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Document deleted successfully")
                .build());
    }
}