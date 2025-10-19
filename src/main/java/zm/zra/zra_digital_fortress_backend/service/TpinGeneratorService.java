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
@Slf4j
class TpinGeneratorService {

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
        // Format: 9 digits + 1 letter (e.g., 123456789A)
        StringBuilder tpin = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            tpin.append(random.nextInt(10));
        }
        char letter = (char) ('A' + random.nextInt(26));
        tpin.append(letter);
        return tpin.toString();
    }
}







