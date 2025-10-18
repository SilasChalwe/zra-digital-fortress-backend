package zm.zra.zra_digital_fortress_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxFilingResponse {

    private String id;
    private String taxType;
    private Integer taxYear;
    private Integer taxPeriod;
    private String status;
    private Double totalIncome;
    private Double totalDeductions;
    private Double taxableIncome;
    private Double taxDue;
    private Double riskScore;
    private Double effectiveTaxRate;
    private String blockchainTxHash;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
}