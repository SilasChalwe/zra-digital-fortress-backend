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
public class BlockchainService {

    @Value("${app.blockchain-service.url}")
    private String blockchainServiceUrl;

    private final WebClient.Builder webClientBuilder;

    public String recordTaxFiling(TaxFiling filing) {
        log.info("Recording tax filing on blockchain: {}", filing.getId());

        try {
            // TODO: Call actual Python blockchain service
            // Map<String, Object> request = Map.of(
            //         "filingId", filing.getId(),
            //         "tpin", filing.getUser().getTpin(),
            //         "taxYear", filing.getTaxYear(),
            //         "taxDue", filing.getTaxDue(),
            //         "timestamp", filing.getSubmittedAt().toString()
            // );
            //
            // WebClient webClient = webClientBuilder.baseUrl(blockchainServiceUrl).build();
            // Map<String, Object> response = webClient.post()
            //         .uri("/api/record-filing")
            //         .bodyValue(request)
            //         .retrieve()
            //         .bodyToMono(Map.class)
            //         .block();
            //
            // return (String) response.get("transactionHash");

            // Mock blockchain transaction hash
            return "0x" + UUID.randomUUID().toString().replace("-", "");
        } catch (Exception e) {
            log.error("Error recording on blockchain", e);
            return "0x" + UUID.randomUUID().toString().replace("-", "");
        }
    }

    public String recordPayment(Payment payment) {
        log.info("Recording payment on blockchain: {}", payment.getId());

        try {
            // TODO: Call actual blockchain service
            // Mock transaction hash
            return "0x" + UUID.randomUUID().toString().replace("-", "");
        } catch (Exception e) {
            log.error("Error recording payment on blockchain", e);
            return "0x" + UUID.randomUUID().toString().replace("-", "");
        }
    }

    public boolean verifyTransaction(String transactionHash) {
        log.info("Verifying blockchain transaction: {}", transactionHash);
        // TODO: Call actual blockchain verification
        return true;
    }
}
