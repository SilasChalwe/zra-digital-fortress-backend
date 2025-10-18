package zm.zra.zra_digital_fortress_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message too long")
    private String message;

    private String language; // EN, BEM, NYA

    private String conversationId;
}