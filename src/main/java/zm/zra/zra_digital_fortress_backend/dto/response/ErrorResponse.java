package zm.zra.zra_digital_fortress_backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private Boolean success = false;
    private String message;
    private String error;
    private Integer status;
    private String path;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private Map<String, String> validationErrors;
}