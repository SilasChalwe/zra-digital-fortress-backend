package zm.zra.zra_digital_fortress_backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ComplianceScoreResponse {

    private Integer overallScore;
    private Integer timelyFilingScore;
    private Integer accuracyScore;
    private Integer paymentHistoryScore;
    private Integer engagementScore;

    private Integer totalFilings;
    private Integer onTimeFilings;
    private Integer lateFilings;

    private Integer totalPayments;
    private Integer onTimePayments;
    private Integer latePayments;

    private Integer consecutiveOnTimeFilings;
    private String badges; // JSON array as string
    private LocalDateTime lastUpdated;
}