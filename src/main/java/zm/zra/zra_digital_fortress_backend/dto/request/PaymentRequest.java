package zm.zra.zra_digital_fortress_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotBlank(message = "Tax filing ID is required")
    private String taxFilingId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // MOBILE_MONEY_MTN, MOBILE_MONEY_AIRTEL, BANK_TRANSFER, CREDIT_CARD, DEBIT_CARD

    // For mobile money
    private String phoneNumber;

    // For bank transfer
    private String bankName;
    private String accountNumber;

    // For card payments
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;

    private String description;
}