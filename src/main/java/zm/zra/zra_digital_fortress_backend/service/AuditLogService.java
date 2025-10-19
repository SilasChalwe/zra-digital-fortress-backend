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
class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void logAction(String userId, String action, AuditLog.EntityType entityType, 
                         String entityId, String changes) {
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .changes(changes)
                .ipAddress("0.0.0.0") // TODO: Get from request
                .status(AuditLog.ActionStatus.SUCCESS)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log created: {} by user {}", action, userId);
    }
}

