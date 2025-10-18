package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "business_taxpayers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTaxpayer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String businessName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private LocalDate dateOfIncorporation;

    @Column(nullable = false)
    private String businessAddress;

    private String postalAddress;

    @Column(nullable = false)
    private String contactPersonName;

    @Column(nullable = false)
    private String contactPersonPhone;

    @Column(nullable = false)
    private String contactPersonEmail;

    @Column(nullable = false)
    private Integer numberOfEmployees;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BusinessSector sector;

    @Column(nullable = false)
    private Double estimatedAnnualTurnover;

    @Column(nullable = false)
    private Boolean vatRegistered = false;

    private String vatRegistrationNumber;

    // Enums
    public enum BusinessType {
        LIMITED_COMPANY,
        PARTNERSHIP,
        SOLE_PROPRIETOR,
        NGO,
        TRUST
    }

    public enum BusinessSector {
        AGRICULTURE,
        MANUFACTURING,
        RETAIL,
        WHOLESALE,
        SERVICES,
        CONSTRUCTION,
        MINING,
        HOSPITALITY,
        TECHNOLOGY,
        EDUCATION,
        HEALTHCARE,
        FINANCE,
        TRANSPORT,
        OTHER
    }
}