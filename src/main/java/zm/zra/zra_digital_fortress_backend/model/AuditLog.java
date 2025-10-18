package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    @Column(nullable = false)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    private String entityId;

    @Column(columnDefinition = "TEXT")
    private String changes; // JSON string of changes

    @Column(nullable = false)
    private String ipAddress;

    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionStatus status;

    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // Enums
    public enum EntityType {
        USER,
        TAX_FILING,
        PAYMENT,
        DOCUMENT,
        SYSTEM
    }

    public enum ActionStatus {
        SUCCESS,
        FAILURE,
        PARTIAL
    }
}