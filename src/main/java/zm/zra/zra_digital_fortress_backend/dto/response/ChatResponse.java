package zm.zra.zra_digital_fortress_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    private String conversationId;
    private String response;
    private List<String> suggestedActions;
    private Boolean escalateToHuman;
    private LocalDateTime timestamp;
}