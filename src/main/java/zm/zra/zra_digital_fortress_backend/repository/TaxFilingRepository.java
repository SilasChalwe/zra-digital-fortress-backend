package zm.zra.zra_digital_fortress_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zm.zra.zra_digital_fortress_backend.model.TaxFiling;
import zm.zra.zra_digital_fortress_backend.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaxFilingRepository extends JpaRepository<TaxFiling, String> {

    List<TaxFiling> findByUserOrderByCreatedAtDesc(User user);

    List<TaxFiling> findByUserAndTaxYear(User user, Integer taxYear);

    Optional<TaxFiling> findByUserAndTaxYearAndTaxPeriodAndTaxType(
            User user, Integer taxYear, Integer taxPeriod, TaxFiling.TaxType taxType);

    List<TaxFiling> findByStatus(TaxFiling.FilingStatus status);

    @Query("SELECT tf FROM TaxFiling tf WHERE tf.riskScore > :threshold ORDER BY tf.riskScore DESC")
    List<TaxFiling> findHighRiskFilings(@Param("threshold") Double threshold);

    @Query("SELECT COUNT(tf) FROM TaxFiling tf WHERE tf.user = :user AND tf.status = 'APPROVED'")
    long countApprovedFilingsByUser(@Param("user") User user);

    @Query("SELECT tf FROM TaxFiling tf WHERE tf.user = :user AND tf.submittedAt BETWEEN :startDate AND :endDate")
    List<TaxFiling> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}