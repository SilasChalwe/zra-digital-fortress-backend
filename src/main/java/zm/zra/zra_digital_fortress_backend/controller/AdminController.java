package zm.zra.zra_digital_fortress_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zm.zra.zra_digital_fortress_backend.dto.response.ApiResponse;
import zm.zra.zra_digital_fortress_backend.model.TaxFiling;
import zm.zra.zra_digital_fortress_backend.repository.TaxFilingRepository;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final UserRepository userRepository;
    private final TaxFilingRepository taxFilingRepository;

    @GetMapping("/statistics")
    @Operation(summary = "Get system statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("activeIndividualTaxpayers", userRepository.countActiveIndividualTaxpayers());
        stats.put("activeBusinessTaxpayers", userRepository.countActiveBusinessTaxpayers());
        stats.put("totalFilings", taxFilingRepository.count());

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @GetMapping("/high-risk-filings")
    @Operation(summary = "Get high risk tax filings")
    public ResponseEntity<ApiResponse<List<TaxFiling>>> getHighRiskFilings(
            @RequestParam(defaultValue = "0.7") Double threshold) {

        List<TaxFiling> highRiskFilings = taxFilingRepository.findHighRiskFilings(threshold);

        return ResponseEntity.ok(ApiResponse.<List<TaxFiling>>builder()
                .success(true)
                .message("High risk filings retrieved successfully")
                .data(highRiskFilings)
                .build());
    }

    @GetMapping("/pending-filings")
    @Operation(summary = "Get pending tax filings for review")
    public ResponseEntity<ApiResponse<List<TaxFiling>>> getPendingFilings() {
        List<TaxFiling> pendingFilings = taxFilingRepository.findByStatus(TaxFiling.FilingStatus.SUBMITTED);

        return ResponseEntity.ok(ApiResponse.<List<TaxFiling>>builder()
                .success(true)
                .message("Pending filings retrieved successfully")
                .data(pendingFilings)
                .build());
    }

    @PutMapping("/filings/{filingId}/approve")
    @Operation(summary = "Approve a tax filing")
    public ResponseEntity<ApiResponse<TaxFiling>> approveFiling(@PathVariable String filingId) {
        TaxFiling filing = taxFilingRepository.findById(filingId)
                .orElseThrow(() -> new RuntimeException("Filing not found"));

        filing.setStatus(TaxFiling.FilingStatus.APPROVED);
        filing = taxFilingRepository.save(filing);

        return ResponseEntity.ok(ApiResponse.<TaxFiling>builder()
                .success(true)
                .message("Filing approved successfully")
                .data(filing)
                .build());
    }

    @PutMapping("/filings/{filingId}/reject")
    @Operation(summary = "Reject a tax filing")
    public ResponseEntity<ApiResponse<TaxFiling>> rejectFiling(@PathVariable String filingId) {
        TaxFiling filing = taxFilingRepository.findById(filingId)
                .orElseThrow(() -> new RuntimeException("Filing not found"));

        filing.setStatus(TaxFiling.FilingStatus.REJECTED);
        filing = taxFilingRepository.save(filing);

        return ResponseEntity.ok(ApiResponse.<TaxFiling>builder()
                .success(true)
                .message("Filing rejected successfully")
                .data(filing)
                .build());
    }
}