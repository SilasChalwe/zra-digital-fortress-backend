package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "individual_taxpayers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndividualTaxpayer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true, length = 15)
    private String nrcNumber; // National Registration Card Number

    @Column(nullable = false)
    private String physicalAddress;

    private String postalAddress;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus employmentStatus;

    private String employerName;

    private String employerTpin;

    @Column(nullable = false)
    private Double estimatedAnnualIncome;

    // Enums
    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public enum EmploymentStatus {
        EMPLOYED,
        SELF_EMPLOYED,
        UNEMPLOYED,
        RETIRED,
        STUDENT
    }
}