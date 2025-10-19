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
class ComplianceService {

    private final UserRepository userRepository;

    public void updateComplianceScore(User user, TaxFiling filing) {
        ComplianceScore score = user.getComplianceScore();
        if (score == null) {
            score = ComplianceScore.builder()
                    .user(user)
                    .overallScore(0)
                    .build();
        }

        // Update filing counts
        score.setTotalFilings(score.getTotalFilings() + 1);
        
        // Check if filed on time
        if (filing.getSubmittedAt().isBefore(LocalDateTime.now())) {
            score.setOnTimeFilings(score.getOnTimeFilings() + 1);
        } else {
            score.setLateFilings(score.getLateFilings() + 1);
        }

        // Calculate scores
        int timelyScore = calculateTimelyScore(score);
        int accuracyScore = calculateAccuracyScore(filing);
        
        score.setTimelyFilingScore(timelyScore);
        score.setAccuracyScore(accuracyScore);
        score.setOverallScore((timelyScore + accuracyScore) / 2);

        log.info("Compliance score updated for user: {}", user.getTpin());
    }

    private int calculateTimelyScore(ComplianceScore score) {
        if (score.getTotalFilings() == 0) return 0;
        return (int) ((double) score.getOnTimeFilings() / score.getTotalFilings() * 100);
    }

    private int calculateAccuracyScore(TaxFiling filing) {
        // Higher accuracy for lower risk scores
        return (int) ((1 - filing.getRiskScore()) * 100);
    }
}