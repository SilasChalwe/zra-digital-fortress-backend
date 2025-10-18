package zm.zra.zra_digital_fortress_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zm.zra.zra_digital_fortress_backend.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByTpin(String tpin);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Boolean existsByEmail(String email);

    Boolean existsByTpin(String tpin);

    Boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailVerificationToken(String token);

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.userType = 'INDIVIDUAL'")
    long countActiveIndividualTaxpayers();

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.userType = 'BUSINESS'")
    long countActiveBusinessTaxpayers();
}