package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Integer overallScore = 0; // 0-100

    @Column(nullable = false)
    private Integer timelyFilingScore = 0;

    @Column(nullable = false)
    private Integer accuracyScore = 0;

    @Column(nullable = false)
    private Integer paymentHistoryScore = 0;

    @Column(nullable = false)
    private Integer engagementScore = 0;

    @Column(nullable = false)
    private Integer totalFilings = 0;

    @Column(nullable = false)
    private Integer onTimeFilings = 0;

    @Column(nullable = false)
    private Integer lateFilings = 0;

    @Column(nullable = false)
    private Integer totalPayments = 0;

    @Column(nullable = false)
    private Integer onTimePayments = 0;

    @Column(nullable = false)
    private Integer latePayments = 0;

    private Integer consecutiveOnTimeFilings = 0;

    @Column(columnDefinition = "TEXT")
    private String badges; // JSON array of earned badges

    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}