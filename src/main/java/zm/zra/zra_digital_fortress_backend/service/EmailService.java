
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
@RequiredArgsConstructor
@Slf4j
class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String email, String token, String tpin) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("ZRA Digital Fortress - Email Verification");
            message.setText(String.format(
                    "Welcome to ZRA Digital Fortress!\n\n" +
                            "Your TPIN: %s\n\n" +
                            "Please verify your email by clicking the link below:\n" +
                            "http://localhost:8080/api/v1/auth/verify-email?token=%s\n\n" +
                            "If you didn't register for this account, please ignore this email.",
                    tpin, token
            ));
            mailSender.send(message);
            log.info("Verification email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String email, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("ZRA Digital Fortress - Password Reset");
            message.setText(String.format(
                    "You requested a password reset.\n\n" +
                            "Click the link below to reset your password:\n" +
                            "http://localhost:3000/reset-password?token=%s\n\n" +
                            "This link will expire in 1 hour.\n\n" +
                            "If you didn't request this, please ignore this email.",
                    resetToken
            ));
            mailSender.send(message);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
        }
    }
}