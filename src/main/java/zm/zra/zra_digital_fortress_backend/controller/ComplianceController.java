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
import zm.zra.zra_digital_fortress_backend.dto.response.ComplianceScoreResponse;
import zm.zra.zra_digital_fortress_backend.model.ComplianceScore;
import zm.zra.zra_digital_fortress_backend.model.User;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/compliance")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Compliance", description = "Compliance and gamification APIs")
public class ComplianceController {

    private final UserRepository userRepository;

    @GetMapping("/score")
    @Operation(summary = "Get compliance score for current user")
    public ResponseEntity<ApiResponse<ComplianceScoreResponse>> getComplianceScore(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ComplianceScore score = user.getComplianceScore();

        ComplianceScoreResponse scoreResponse;
        if (score == null) {
            scoreResponse = ComplianceScoreResponse.builder()
                    .overallScore(0)
                    .timelyFilingScore(0)
                    .accuracyScore(0)
                    .paymentHistoryScore(0)
                    .engagementScore(0)
                    .totalFilings(0)
                    .onTimeFilings(0)
                    .lateFilings(0)
                    .totalPayments(0)
                    .onTimePayments(0)
                    .latePayments(0)
                    .consecutiveOnTimeFilings(0)
                    .badges("[]")
                    .lastUpdated(LocalDateTime.now())
                    .build();
        } else {
            scoreResponse = ComplianceScoreResponse.builder()
                    .overallScore(score.getOverallScore())
                    .timelyFilingScore(score.getTimelyFilingScore())
                    .accuracyScore(score.getAccuracyScore())
                    .paymentHistoryScore(score.getPaymentHistoryScore())
                    .engagementScore(score.getEngagementScore())
                    .totalFilings(score.getTotalFilings())
                    .onTimeFilings(score.getOnTimeFilings())
                    .lateFilings(score.getLateFilings())
                    .totalPayments(score.getTotalPayments())
                    .onTimePayments(score.getOnTimePayments())
                    .latePayments(score.getLatePayments())
                    .consecutiveOnTimeFilings(score.getConsecutiveOnTimeFilings())
                    .badges(score.getBadges())
                    .lastUpdated(LocalDateTime.now()) // Or use score.getLastUpdated() if available in entity
                    .build();
        }

        return ResponseEntity.ok(ApiResponse.<ComplianceScoreResponse>builder()
                .success(true)
                .message("Compliance score retrieved successfully")
                .data(scoreResponse)
                .build());
    }

    @GetMapping("/badges")
    @Operation(summary = "Get earned badges")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBadges(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ComplianceScore score = user.getComplianceScore();

        Map<String, Object> badgeInfo = new HashMap<>();
        badgeInfo.put("badges", score != null ? score.getBadges() : "[]");
        badgeInfo.put("totalBadges", score != null ? parseBadgeCount(score.getBadges()) : 0);
        badgeInfo.put("consecutiveOnTimeFilings", score != null ? score.getConsecutiveOnTimeFilings() : 0);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Badges retrieved successfully")
                .data(badgeInfo)
                .build());
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get compliance leaderboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLeaderboard() {

        // Mock leaderboard data
        Map<String, Object> leaderboard = new HashMap<>();
        leaderboard.put("message", "Leaderboard feature coming soon");
        leaderboard.put("topTaxpayers", new java.util.ArrayList<>());

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Leaderboard retrieved successfully")
                .data(leaderboard)
                .build());
    }

    @GetMapping("/stats")
    @Operation(summary = "Get detailed compliance statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getComplianceStats(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ComplianceScore score = user.getComplianceScore();

        Map<String, Object> stats = new HashMap<>();
        if (score != null) {
            stats.put("overallScore", score.getOverallScore());
            stats.put("timelyFilingScore", score.getTimelyFilingScore());
            stats.put("accuracyScore", score.getAccuracyScore());
            stats.put("paymentHistoryScore", score.getPaymentHistoryScore());
            stats.put("engagementScore", score.getEngagementScore());
            stats.put("totalFilings", score.getTotalFilings());
            stats.put("onTimeFilings", score.getOnTimeFilings());
            stats.put("lateFilings", score.getLateFilings());
            stats.put("totalPayments", score.getTotalPayments());
            stats.put("onTimePayments", score.getOnTimePayments());
            stats.put("latePayments", score.getLatePayments());
            stats.put("complianceLevel", getComplianceLevel(score.getOverallScore()));
        } else {
            // Provide default values when score is null
            stats.put("overallScore", 0);
            stats.put("timelyFilingScore", 0);
            stats.put("accuracyScore", 0);
            stats.put("paymentHistoryScore", 0);
            stats.put("engagementScore", 0);
            stats.put("totalFilings", 0);
            stats.put("onTimeFilings", 0);
            stats.put("lateFilings", 0);
            stats.put("totalPayments", 0);
            stats.put("onTimePayments", 0);
            stats.put("latePayments", 0);
            stats.put("complianceLevel", "NEW");
        }

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Compliance statistics retrieved successfully")
                .data(stats)
                .build());
    }

    private int parseBadgeCount(String badgesJson) {
        if (badgesJson == null || badgesJson.equals("[]")) {
            return 0;
        }
        try {
            return badgesJson.split(",").length;
        } catch (Exception e) {
            return 0;
        }
    }

    private String getComplianceLevel(Integer score) {
        if (score == null) return "NEW";
        if (score >= 90) return "EXCELLENT";
        if (score >= 70) return "GOOD";
        if (score >= 50) return "FAIR";
        return "POOR";
    }
}