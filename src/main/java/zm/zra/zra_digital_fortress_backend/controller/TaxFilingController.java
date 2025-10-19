package zm.zra.zra_digital_fortress_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zm.zra.zra_digital_fortress_backend.dto.request.TaxFilingRequest;
import zm.zra.zra_digital_fortress_backend.dto.response.ApiResponse;
import zm.zra.zra_digital_fortress_backend.dto.response.TaxFilingResponse;
import zm.zra.zra_digital_fortress_backend.model.User;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;
import zm.zra.zra_digital_fortress_backend.service.TaxFilingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tax-filings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tax Filing", description = "Tax filing management APIs")
public class TaxFilingController {

    private final TaxFilingService taxFilingService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Submit a new tax filing")
    public ResponseEntity<ApiResponse<TaxFilingResponse>> submitTaxFiling(
            @Valid @RequestBody TaxFilingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaxFilingResponse response = taxFilingService.submitTaxFiling(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TaxFilingResponse>builder()
                        .success(true)
                        .message(request.getSaveDraft() ? "Tax filing saved as draft" : "Tax filing submitted successfully")
                        .data(response)
                        .build());
    }

    @GetMapping
    @Operation(summary = "Get all tax filings for current user")
    public ResponseEntity<ApiResponse<List<TaxFilingResponse>>> getUserFilings(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TaxFilingResponse> filings = taxFilingService.getUserFilings(user.getId());

        return ResponseEntity.ok(ApiResponse.<List<TaxFilingResponse>>builder()
                .success(true)
                .message("Tax filings retrieved successfully")
                .data(filings)
                .build());
    }

    @GetMapping("/{filingId}")
    @Operation(summary = "Get tax filing by ID")
    public ResponseEntity<ApiResponse<TaxFilingResponse>> getFilingById(
            @PathVariable String filingId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaxFilingResponse filing = taxFilingService.getFilingById(user.getId(), filingId);

        return ResponseEntity.ok(ApiResponse.<TaxFilingResponse>builder()
                .success(true)
                .message("Tax filing retrieved successfully")
                .data(filing)
                .build());
    }
}