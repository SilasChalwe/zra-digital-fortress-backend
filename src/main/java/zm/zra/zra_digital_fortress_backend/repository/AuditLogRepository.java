package zm.zra.zra_digital_fortress_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zm.zra.zra_digital_fortress_backend.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    List<AuditLog> findByUserIdOrderByTimestampDesc(String userId);

    List<AuditLog> findByEntityTypeAndEntityId(AuditLog.EntityType entityType, String entityId);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}