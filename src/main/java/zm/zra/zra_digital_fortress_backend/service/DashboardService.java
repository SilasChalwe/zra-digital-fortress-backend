package zm.zra.zra_digital_fortress_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zm.zra.zra_digital_fortress_backend.dto.response.DashboardResponse;
import zm.zra.zra_digital_fortress_backend.exception.ResourceNotFoundException;
import zm.zra.zra_digital_fortress_backend.model.*;
import zm.zra.zra_digital_fortress_backend.repository.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserRepository userRepository;
    private final IndividualTaxpayerRepository individualTaxpayerRepository;
    private final BusinessTaxpayerRepository businessTaxpayerRepository;
    private final TaxFilingRepository taxFilingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    public DashboardResponse getDashboardData(String userId) {
        log.info("Getting dashboard data for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return DashboardResponse.builder()
                .userSummary(buildUserSummary(user))
                .complianceSummary(buildComplianceSummary(user))
                .upcomingObligations(buildUpcomingObligations(user))
                .recentActivities(buildRecentActivities(user))
                .notifications(buildNotifications(user))
                .paymentSummary(buildPaymentSummary(user))
                .build();
    }

    private DashboardResponse.UserSummary buildUserSummary(User user) {
        String name = "";
        if (user.getUserType() == User.UserType.INDIVIDUAL) {
            individualTaxpayerRepository.findByUser(user).ifPresent(individual -> {
            });
        } else if (user.getUserType() == User.UserType.BUSINESS) {
            businessTaxpayerRepository.findByUser(user).ifPresent(business -> {
            });
        }

        return DashboardResponse.UserSummary.builder()
                .tpin(user.getTpin())
                .name(getUserDisplayName(user))
                .userType(user.getUserType().name())
                .lastLogin(user.getLastLogin())
                .build();
    }

    private String getUserDisplayName(User user) {
        if (user.getUserType() == User.UserType.INDIVIDUAL) {
            return individualTaxpayerRepository.findByUser(user)
                    .map(ind -> ind.getFirstName() + " " + ind.getLastName())
                    .orElse("User");
        } else {
            return businessTaxpayerRepository.findByUser(user)
                    .map(BusinessTaxpayer::getBusinessName)
                    .orElse("Business");
        }
    }

    private DashboardResponse.ComplianceSummary buildComplianceSummary(User user) {
        ComplianceScore score = user.getComplianceScore();
        
        if (score == null) {
            return DashboardResponse.ComplianceSummary.builder()
                    .overallScore(0)
                    .timelyFilingScore(0)
                    .accuracyScore(0)
                    .paymentHistoryScore(0)
                    .complianceLevel("NEW")
                    .badges(new ArrayList<>())
                    .build();
        }

        String level = getComplianceLevel(score.getOverallScore());
        List<String> badges = parseBadges(score.getBadges());

        return DashboardResponse.ComplianceSummary.builder()
                .overallScore(score.getOverallScore())
                .timelyFilingScore(score.getTimelyFilingScore())
                .accuracyScore(score.getAccuracyScore())
                .paymentHistoryScore(score.getPaymentHistoryScore())
                .complianceLevel(level)
                .badges(badges)
                .build();
    }

    private String getComplianceLevel(Integer score) {
        if (score >= 90) return "EXCELLENT";
        if (score >= 70) return "GOOD";
        if (score >= 50) return "FAIR";
        return "POOR";
    }

    private List<String> parseBadges(String badgesJson) {
        try {
            if (badgesJson != null && !badgesJson.isEmpty()) {
                return objectMapper.readValue(badgesJson, new TypeReference<List<String>>() {});
            }
        } catch (Exception e) {
            log.error("Error parsing badges", e);
        }
        return new ArrayList<>();
    }

    private List<DashboardResponse.TaxObligation> buildUpcomingObligations(User user) {
        List<DashboardResponse.TaxObligation> obligations = new ArrayList<>();

        // Mock upcoming obligations
        LocalDateTime nextFilingDue = LocalDateTime.now().plusDays(30);
        
        obligations.add(DashboardResponse.TaxObligation.builder()
                .taxType("INCOME_TAX")
                .description("Annual Income Tax Filing")
                .dueDate(nextFilingDue)
                .estimatedAmount(0.0)
                .status("PENDING")
                .daysRemaining((int) ChronoUnit.DAYS.between(LocalDateTime.now(), nextFilingDue))
                .build());

        return obligations;
    }

    private List<DashboardResponse.RecentActivity> buildRecentActivities(User user) {
        List<DashboardResponse.RecentActivity> activities = new ArrayList<>();

        // Get recent filings
        List<TaxFiling> recentFilings = taxFilingRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        for (TaxFiling filing : recentFilings) {
            activities.add(DashboardResponse.RecentActivity.builder()
                    .activityType("TAX_FILING")
                    .description("Filed " + filing.getTaxType() + " for " + filing.getTaxYear())
                    .timestamp(filing.getCreatedAt())
                    .status(filing.getStatus().name())
                    .build());
        }

        // Get recent payments
        List<Payment> recentPayments = paymentRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        for (Payment payment : recentPayments) {
            activities.add(DashboardResponse.RecentActivity.builder()
                    .activityType("PAYMENT")
                    .description("Payment of ZMW " + payment.getAmount())
                    .timestamp(payment.getCreatedAt())
                    .status(payment.getStatus().name())
                    .build());
        }

        return activities.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<DashboardResponse.NotificationSummary> buildNotifications(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        return notifications.stream()
                .map(n -> DashboardResponse.NotificationSummary.builder()
                        .id(n.getId())
                        .type(n.getType().name())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .read(n.getRead())
                        .createdAt(n.getCreatedAt())
                        .priority(n.getPriority() != null ? n.getPriority().name() : "MEDIUM")
                        .build())
                .collect(Collectors.toList());
    }

    private DashboardResponse.PaymentSummary buildPaymentSummary(User user) {
        Double totalPaid = paymentRepository.getTotalPaymentsByUser(user);
        if (totalPaid == null) totalPaid = 0.0;

        List<Payment> payments = paymentRepository.findByUserOrderByCreatedAtDesc(user);
        int paymentsThisYear = (int) payments.stream()
                .filter(p -> p.getCreatedAt().getYear() == LocalDateTime.now().getYear())
                .count();

        LocalDateTime lastPaymentDate = payments.isEmpty() ? null : payments.get(0).getCreatedAt();

        // Calculate total due (mock)
        Double totalDue = 0.0;

        return DashboardResponse.PaymentSummary.builder()
                .totalPaid(totalPaid)
                .totalDue(totalDue)
                .paymentsThisYear(paymentsThisYear)
                .lastPaymentDate(lastPaymentDate)
                .build();
    }
}