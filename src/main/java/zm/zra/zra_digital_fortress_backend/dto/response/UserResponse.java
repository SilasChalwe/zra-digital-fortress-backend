package zm.zra.zra_digital_fortress_backend.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String id;
    private String tpin;
    private String email;
    private String phoneNumber;
    private String userType;
    private String accountStatus;
    private Boolean mfaEnabled;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    // Individual fields
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String nrcNumber;
    private String physicalAddress;
    private String gender;
    private String employmentStatus;

    // Business fields
    private String businessName;
    private String businessType;
    private String registrationNumber;
    private LocalDate dateOfIncorporation;
    private String businessAddress;
    private Integer numberOfEmployees;
    private String sector;
    private Boolean vatRegistered;

    // Compliance
    private Integer complianceScore;
}