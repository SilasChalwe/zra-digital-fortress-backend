package zm.zra.zra_digital_fortress_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zm.zra.zra_digital_fortress_backend.model.BusinessTaxpayer;
import zm.zra.zra_digital_fortress_backend.model.User;

import java.util.Optional;

@Repository
public interface BusinessTaxpayerRepository extends JpaRepository<BusinessTaxpayer, String> {

    Optional<BusinessTaxpayer> findByUser(User user);

    Optional<BusinessTaxpayer> findByRegistrationNumber(String registrationNumber);

    Boolean existsByRegistrationNumber(String registrationNumber);
}