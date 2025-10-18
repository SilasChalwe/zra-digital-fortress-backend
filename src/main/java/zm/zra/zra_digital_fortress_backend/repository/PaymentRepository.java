package zm.zra.zra_digital_fortress_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zm.zra.zra_digital_fortress_backend.model.Payment;
import zm.zra.zra_digital_fortress_backend.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByUserOrderByCreatedAtDesc(User user);

    Optional<Payment> findByTransactionReference(String transactionReference);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user = :user AND p.status = 'COMPLETED'")
    Double getTotalPaymentsByUser(@Param("user") User user);

    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.paidAt BETWEEN :startDate AND :endDate")
    List<Payment> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}