package zm.zra.zra_digital_fortress_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterIndividualRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    private String lastName;

    private String middleName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "NRC number is required")
    @Pattern(regexp = "^\\d{6}/\\d{2}/\\d$", message = "Invalid NRC format. Expected format: 123456/78/1")
    private String nrcNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+260[0-9]{9}$", message = "Invalid Zambian phone number. Expected format: +260XXXXXXXXX")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;

    @NotBlank(message = "Physical address is required")
    private String physicalAddress;

    private String postalAddress;

    @NotBlank(message = "Gender is required")
    private String gender; // MALE, FEMALE, OTHER

    @NotBlank(message = "Employment status is required")
    private String employmentStatus; // EMPLOYED, SELF_EMPLOYED, UNEMPLOYED, RETIRED, STUDENT

    private String employerName;

    private String employerTpin;

    @NotNull(message = "Estimated annual income is required")
    @Positive(message = "Income must be positive")
    private Double estimatedAnnualIncome;
}