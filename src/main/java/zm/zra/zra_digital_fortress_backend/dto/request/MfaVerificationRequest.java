package zm.zra.zra_digital_fortress_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MfaVerificationRequest {

    @NotBlank(message = "TPIN or email is required")
    private String tpinOrEmail;

    @NotBlank(message = "MFA code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "MFA code must be 6 digits")
    private String mfaCode;
}