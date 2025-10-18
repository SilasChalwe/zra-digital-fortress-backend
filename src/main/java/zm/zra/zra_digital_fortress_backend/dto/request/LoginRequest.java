package zm.zra.zra_digital_fortress_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "TPIN or email is required")
    private String tpinOrEmail;

    @NotBlank(message = "Password is required")
    private String password;

    private String mfaCode;
}