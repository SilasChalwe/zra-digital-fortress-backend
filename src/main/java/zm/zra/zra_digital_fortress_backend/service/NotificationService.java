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
class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    public void sendFilingConfirmation(User user, TaxFiling filing) {
        Notification notification = Notification.builder()
                .user(user)
                .type(Notification.NotificationType.FILING_APPROVED)
                .title("Tax Filing Submitted")
                .message(String.format(
                        "Your %s tax filing for period %d/%d has been successfully submitted.",
                        filing.getTaxType(), filing.getTaxPeriod(), filing.getTaxYear()
                ))
                .priority(Notification.Priority.HIGH)
                .read(false)
                .build();

        notificationRepository.save(notification);
        log.info("Filing confirmation notification sent to user: {}", user.getTpin());
    }

    public void sendPaymentConfirmation(User user, Payment payment) {
        Notification notification = Notification.builder()
                .user(user)
                .type(Notification.NotificationType.PAYMENT_CONFIRMED)
                .title("Payment Confirmed")
                .message(String.format(
                        "Your payment of ZMW %.2f has been successfully processed.",
                        payment.getAmount()
                ))
                .priority(Notification.Priority.HIGH)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    public void sendFilingReminder(User user, String taxType, LocalDateTime dueDate) {
        Notification notification = Notification.builder()
                .user(user)
                .type(Notification.NotificationType.FILING_REMINDER)
                .title("Filing Deadline Approaching")
                .message(String.format(
                        "Your %s filing is due on %s. Please file before the deadline to avoid penalties.",
                        taxType, dueDate
                ))
                .priority(Notification.Priority.MEDIUM)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }
}
