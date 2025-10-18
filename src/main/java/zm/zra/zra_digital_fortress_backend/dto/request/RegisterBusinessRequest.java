package zm.zra.zra_digital_fortress_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterBusinessRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Business type is required")
    private String businessType; // LIMITED_COMPANY, PARTNERSHIP, SOLE_PROPRIETOR, NGO, TRUST

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotNull(message = "Date of incorporation is required")
    @Past(message = "Date of incorporation must be in the past")
    private LocalDate dateOfIncorporation;

    @NotBlank(message = "Business address is required")
    private String businessAddress;

    private String postalAddress;

    @NotBlank(message = "Contact person name is required")
    private String contactPersonName;

    @NotBlank(message = "Contact person phone is required")
    @Pattern(regexp = "^\\+260[0-9]{9}$")
    private String contactPersonPhone;

    @NotBlank(message = "Contact person email is required")
    @Email
    private String contactPersonEmail;

    @NotBlank(message = "Main email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+260[0-9]{9}$", message = "Invalid Zambian phone number")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Number of employees is required")
    @Positive
    private Integer numberOfEmployees;

    @NotBlank(message = "Business sector is required")
    private String sector;

    @NotNull(message = "Estimated annual turnover is required")
    @Positive
    private Double estimatedAnnualTurnover;

    private Boolean vatRegistered = false;

    private String vatRegistrationNumber;
}