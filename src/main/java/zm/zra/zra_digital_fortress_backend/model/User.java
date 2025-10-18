package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 10)
    private String tpin; // Taxpayer Identification Number

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType; // INDIVIDUAL, BUSINESS, ZRA_STAFF, ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status; // PENDING, ACTIVE, SUSPENDED, CLOSED

    @Column(nullable = false)
    private Boolean mfaEnabled = false;

    private String mfaSecret;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column(nullable = false)
    private Boolean phoneVerified = false;

    private String emailVerificationToken;
    
    private String phoneVerificationCode;

    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IndividualTaxpayer individualTaxpayer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BusinessTaxpayer businessTaxpayer;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaxFiling> taxFilings = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Notification> notifications = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ComplianceScore complianceScore;

    // Enums
    public enum UserType {
        INDIVIDUAL,
        BUSINESS,
        ZRA_STAFF,
        ADMIN
    }

    public enum AccountStatus {
        PENDING,
        ACTIVE,
        SUSPENDED,
        CLOSED
    }
}