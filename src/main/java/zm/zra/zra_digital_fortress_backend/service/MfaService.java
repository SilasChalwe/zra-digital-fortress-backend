package zm.zra.zra_digital_fortress_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import zm.zra.zra_digital_fortress_backend.model.*;
import zm.zra.zra_digital_fortress_backend.repository.*;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;



@Service
@Slf4j
class MfaService {

    public String generateMfaSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String generateQrCodeUrl(String secret, String userEmail) {
        return String.format(
                "otpauth://totp/ZRA Digital Fortress:%s?secret=%s&issuer=ZRA",
                userEmail, secret
        );
    }

    public boolean validateMfaCode(String secret, String code) {
        // TODO: Implement TOTP validation using GoogleAuthenticator library
        log.info("Validating MFA code");
        return true;
    }
}