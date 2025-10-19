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
public class PaymentGatewayService {

    private final MobileMoneyService mobileMoneyService;

    public Map<String, Object> processPayment(Payment payment, Map<String, String> paymentDetails) {
        log.info("Processing payment: {}", payment.getId());

        try {
            Map<String, Object> result = new HashMap<>();

            switch (payment.getPaymentMethod()) {
                case MOBILE_MONEY_MTN:
                case MOBILE_MONEY_AIRTEL:
                    result = mobileMoneyService.processMobileMoneyPayment(
                            payment.getAmount(),
                            paymentDetails.get("phoneNumber"),
                            payment.getPaymentMethod().name()
                    );
                    break;

                case BANK_TRANSFER:
                    result = processBankTransfer(payment, paymentDetails);
                    break;

                case CREDIT_CARD:
                case DEBIT_CARD:
                    result = processCardPayment(payment, paymentDetails);
                    break;

                default:
                    result.put("success", false);
                    result.put("message", "Unsupported payment method");
            }

            return result;
        } catch (Exception e) {
            log.error("Payment processing failed", e);
            return Map.of(
                    "success", false,
                    "message", "Payment processing failed: " + e.getMessage()
            );
        }
    }

    private Map<String, Object> processBankTransfer(Payment payment, Map<String, String> details) {
        // TODO: Integrate with bank payment API
        log.info("Processing bank transfer");
        return Map.of(
                "success", true,
                "transactionId", "BTX" + System.currentTimeMillis(),
                "message", "Bank transfer initiated"
        );
    }

    private Map<String, Object> processCardPayment(Payment payment, Map<String, String> details) {
        // TODO: Integrate with card payment gateway
        log.info("Processing card payment");
        return Map.of(
                "success", true,
                "transactionId", "CPX" + System.currentTimeMillis(),
                "message", "Card payment processed"
        );
    }
}

