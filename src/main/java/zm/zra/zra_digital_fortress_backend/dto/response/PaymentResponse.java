package zm.zra.zra_digital_fortress_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String id;
    private String transactionReference;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String taxFilingId;
    private String blockchainTxHash;
    private String receiptUrl;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}