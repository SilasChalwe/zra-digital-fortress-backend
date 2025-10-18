package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tax_filings")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxFiling {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaxType taxType;

    @Column(nullable = false)
    private Integer taxYear;

    @Column(nullable = false)
    private Integer taxPeriod; // Month or Quarter

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilingStatus status;

    @Column(nullable = false)
    private Double totalIncome;

    @Column(nullable = false)
    private Double totalDeductions;

    @Column(nullable = false)
    private Double taxableIncome;

    @Column(nullable = false)
    private Double taxDue;

    @Column(nullable = false)
    private Double riskScore; // 0.0 to 1.0

    private String riskFactors; // JSON string of risk analysis

    private String blockchainTxHash; // Transaction hash from blockchain

    @Column(columnDefinition = "TEXT")
    private String filingData; // JSON string of complete filing data

    private LocalDateTime submittedAt;

    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Enums
    public enum TaxType {
        INCOME_TAX,
        VAT,
        COMPANY_TAX,
        WITHHOLDING_TAX,
        PROPERTY_TRANSFER_TAX
    }

    public enum FilingStatus {
        DRAFT,
        SUBMITTED,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        AMENDED
    }
}