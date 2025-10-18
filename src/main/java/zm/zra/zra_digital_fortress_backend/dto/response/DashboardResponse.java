package zm.zra.zra_digital_fortress_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private UserSummary userSummary;
    private ComplianceSummary complianceSummary;
    private List<TaxObligation> upcomingObligations;
    private List<RecentActivity> recentActivities;
    private List<NotificationSummary> notifications;
    private PaymentSummary paymentSummary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private String tpin;
        private String name;
        private String userType;
        private LocalDateTime lastLogin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplianceSummary {
        private Integer overallScore;
        private Integer timelyFilingScore;
        private Integer accuracyScore;
        private Integer paymentHistoryScore;
        private String complianceLevel; // EXCELLENT, GOOD, FAIR, POOR
        private List<String> badges;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxObligation {
        private String taxType;
        private String description;
        private LocalDateTime dueDate;
        private Double estimatedAmount;
        private String status; // PENDING, OVERDUE, COMPLETED
        private Integer daysRemaining;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String activityType;
        private String description;
        private LocalDateTime timestamp;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationSummary {
        private String id;
        private String type;
        private String title;
        private String message;
        private Boolean read;
        private LocalDateTime createdAt;
        private String priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentSummary {
        private Double totalPaid;
        private Double totalDue;
        private Integer paymentsThisYear;
        private LocalDateTime lastPaymentDate;
    }
}