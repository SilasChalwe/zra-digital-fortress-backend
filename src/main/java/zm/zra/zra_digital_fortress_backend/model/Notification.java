package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private Boolean read = false;

    private LocalDateTime readAt;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private String actionUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum NotificationType {
        FILING_REMINDER,
        PAYMENT_DUE,
        PAYMENT_CONFIRMED,
        FILING_APPROVED,
        FILING_REJECTED,
        SYSTEM_ALERT,
        COMPLIANCE_UPDATE,
        SECURITY_ALERT
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}