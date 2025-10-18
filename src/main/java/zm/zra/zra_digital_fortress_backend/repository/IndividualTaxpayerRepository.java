package zm.zra.zra_digital_fortress_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zm.zra.zra_digital_fortress_backend.model.IndividualTaxpayer;
import zm.zra.zra_digital_fortress_backend.model.User;

import java.util.Optional;

@Repository
public interface IndividualTaxpayerRepository extends JpaRepository<IndividualTaxpayer, String> {

    Optional<IndividualTaxpayer> findByUser(User user);

    Optional<IndividualTaxpayer> findByNrcNumber(String nrcNumber);

    Boolean existsByNrcNumber(String nrcNumber);
}