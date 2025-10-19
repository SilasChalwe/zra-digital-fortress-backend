package zm.zra.zra_digital_fortress_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zm.zra.zra_digital_fortress_backend.dto.response.ApiResponse;
import zm.zra.zra_digital_fortress_backend.model.Payment;
import zm.zra.zra_digital_fortress_backend.model.TaxFiling;
import zm.zra.zra_digital_fortress_backend.model.User;
import zm.zra.zra_digital_fortress_backend.repository.PaymentRepository;
import zm.zra.zra_digital_fortress_backend.repository.TaxFilingRepository;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reports", description = "Report generation APIs")
public class ReportController {

    private final TaxFilingRepository taxFilingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @GetMapping("/tax-history")
    @Operation(summary = "Get tax filing history report")
    public ResponseEntity<ApiResponse<List<TaxFiling>>> getTaxHistory(
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TaxFiling> filings;
        if (year != null) {
            filings = taxFilingRepository.findByUserAndTaxYear(user, year);
        } else {
            filings = taxFilingRepository.findByUserOrderByCreatedAtDesc(user);
        }

        return ResponseEntity.ok(ApiResponse.<List<TaxFiling>>builder()
                .success(true)
                .message("Tax history retrieved successfully")
                .data(filings)
                .build());
    }

    @GetMapping("/payment-history")
    @Operation(summary = "Get payment history report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Payment> payments;
        if (startDate != null && endDate != null) {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            payments = paymentRepository.findByUserAndDateRange(user, start, end);
        } else {
            payments = paymentRepository.findByUserOrderByCreatedAtDesc(user);
        }

        Double totalPaid = payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();

        Map<String, Object> report = new HashMap<>();
        report.put("payments", payments);
        report.put("totalPaid", totalPaid);
        report.put("paymentCount", payments.size());

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Payment history retrieved successfully")
                .data(report)
                .build());
    }

    @GetMapping("/compliance-report")
    @Operation(summary = "Get compliance report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getComplianceReport(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> report = new HashMap<>();
        
        if (user.getComplianceScore() != null) {
            report.put("overallScore", user.getComplianceScore().getOverallScore());
            report.put("timelyFilingScore", user.getComplianceScore().getTimelyFilingScore());
            report.put("accuracyScore", user.getComplianceScore().getAccuracyScore());
            report.put("paymentHistoryScore", user.getComplianceScore().getPaymentHistoryScore());
            report.put("totalFilings", user.getComplianceScore().getTotalFilings());
            report.put("onTimeFilings", user.getComplianceScore().getOnTimeFilings());
            report.put("lateFilings", user.getComplianceScore().getLateFilings());
        }

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Compliance report retrieved successfully")
                .data(report)
                .build());
    }
}