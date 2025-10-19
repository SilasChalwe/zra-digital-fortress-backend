package zm.zra.zra_digital_fortress_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;

import java.util.Random;

@Service
@Slf4j
public class TpinGeneratorService {

    private final UserRepository userRepository;
    private final Random random = new Random();

    public TpinGeneratorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateTpin() {
        String tpin;
        do {
            tpin = generateRandomTpin();
        } while (userRepository.existsByTpin(tpin));
        return tpin;
    }

    private String generateRandomTpin() {
        // Generate a 10-digit numeric TPIN (e.g., 1234567890)
        StringBuilder tpin = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            tpin.append(random.nextInt(10)); // digits 0–9
        }
        return tpin.toString();
    }
}
