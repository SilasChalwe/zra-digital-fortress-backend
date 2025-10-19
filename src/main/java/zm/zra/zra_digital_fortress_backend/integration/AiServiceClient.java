package zm.zra.zra_digital_fortress_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import zm.zra.zra_digital_fortress_backend.model.Payment;
import zm.zra.zra_digital_fortress_backend.model.TaxFiling;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceClient {

    @Value("${app.ai-service.url}")
    private String aiServiceUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public Map<String, Object> assessFraudRisk(TaxFiling filing) {
        log.info("Assessing fraud risk for filing: {}", filing.getId());

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("totalIncome", filing.getTotalIncome());
            request.put("totalDeductions", filing.getTotalDeductions());
            request.put("taxableIncome", filing.getTaxableIncome());
            request.put("taxDue", filing.getTaxDue());
            request.put("taxYear", filing.getTaxYear());

            // TODO: Call actual Python AI service
            // WebClient webClient = webClientBuilder.baseUrl(aiServiceUrl).build();
            // Map<String, Object> response = webClient.post()
            //         .uri("/api/assess-fraud")
            //         .bodyValue(request)
            //         .retrieve()
            //         .bodyToMono(Map.class)
            //         .block();

            // Mock response for now
            Map<String, Object> response = new HashMap<>();
            response.put("riskScore", calculateMockRiskScore(filing));
            response.put("factors", Map.of(
                    "incomeConsistency", "normal",
                    "deductionRatio", "acceptable",
                    "historicalPattern", "consistent"
            ));

            return response;
        } catch (Exception e) {
            log.error("Error assessing fraud risk", e);
            return Map.of("riskScore", 0.1, "factors", Map.of());
        }
    }

    private double calculateMockRiskScore(TaxFiling filing) {
        // Simple risk calculation based on deduction ratio
        if (filing.getTotalIncome() == 0) return 0.5;
        
        double deductionRatio = filing.getTotalDeductions() / filing.getTotalIncome();
        
        if (deductionRatio > 0.5) return 0.7; // High risk
        if (deductionRatio > 0.3) return 0.4; // Medium risk
        return 0.1; // Low risk
    }

    public Map<String, Object> getChatbotResponse(String message, String language) {
        log.info("Getting chatbot response for message: {}", message);

        // TODO: Call actual AI chatbot service
        // Mock response
        Map<String, Object> response = new HashMap<>();
        response.put("response", "I'm here to help with your tax queries. How can I assist you today?");
        response.put("suggestedActions", new String[]{"File taxes", "Check payment status", "View compliance score"});
        response.put("escalateToHuman", false);

        return response;
    }
}


