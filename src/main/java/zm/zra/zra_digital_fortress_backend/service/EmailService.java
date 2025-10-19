package zm.zra.zra_digital_fortress_backend.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;   

    @Async
    public void sendVerificationEmail(String email, String token, String tpin) {
        try {
            String verificationLink = baseUrl + "/api/v1/auth/verify-email?token=" + token;

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("ZRA Digital Fortress - Email Verification");

            Context context = new Context();
            context.setVariable("tpin", tpin);
            context.setVariable("verificationLink", verificationLink);

            String htmlContent = templateEngine.process("email/registration-confirmation", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Verification email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String email, String resetToken) {
        try {
            String resetLink = baseUrl + "/reset-password?token=" + resetToken;

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("ZRA Digital Fortress - Password Reset");

            Context context = new Context();
            context.setVariable("resetLink", resetLink);

            String htmlContent = templateEngine.process("email/reset", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
        }
    }
}