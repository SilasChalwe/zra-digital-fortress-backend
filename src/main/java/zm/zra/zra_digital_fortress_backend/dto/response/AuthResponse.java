package zm.zra.zra_digital_fortress_backend.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserResponse user;
    private Boolean mfaRequired;
    private String mfaQrCode; // For MFA setup

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private String id;
        private String tpin;
        private String email;
        private String phoneNumber;
        private String userType;
        private String accountStatus;
        private Boolean mfaEnabled;
        private Boolean emailVerified;
        private Boolean phoneVerified;
        private String firstName; // For individual
        private String lastName; // For individual
        private String businessName; // For business
    }
}