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
class SmsService {

    @Async
    public void sendSms(String phoneNumber, String message) {
        // TODO: Integrate with SMS gateway (e.g., Twilio, Africa's Talking)
        log.info("SMS sent to {}: {}", phoneNumber, message);
    }

    public void sendVerificationCode(String phoneNumber, String code) {
        String message = String.format("Your ZRA Digital Fortress verification code is: %s", code);
        sendSms(phoneNumber, message);
    }
}
