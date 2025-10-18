package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_filing_id")
    private TaxFiling taxFiling;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(unique = true)
    private String transactionReference;

    private String externalTransactionId; // From payment gateway

    private String paymentDetails; // JSON string with method-specific details

    private String blockchainTxHash;

    @Column(columnDefinition = "TEXT")
    private String receiptData; // Generated receipt information

    private LocalDateTime paidAt;

    private String failureReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum PaymentMethod {
        MOBILE_MONEY_MTN,
        MOBILE_MONEY_AIRTEL,
        BANK_TRANSFER,
        CREDIT_CARD,
        DEBIT_CARD,
        CASH
    }

    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELLED
    }
}