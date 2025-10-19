package zm.zra.zra_digital_fortress_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zm.zra.zra_digital_fortress_backend.dto.request.PaymentRequest;
import zm.zra.zra_digital_fortress_backend.dto.response.PaymentResponse;
import zm.zra.zra_digital_fortress_backend.exception.BadRequestException;
import zm.zra.zra_digital_fortress_backend.exception.ResourceNotFoundException;
import zm.zra.zra_digital_fortress_backend.integration.BlockchainService;
import zm.zra.zra_digital_fortress_backend.integration.PaymentGatewayService;
import zm.zra.zra_digital_fortress_backend.model.*;
import zm.zra.zra_digital_fortress_backend.repository.PaymentRepository;
import zm.zra.zra_digital_fortress_backend.repository.TaxFilingRepository;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TaxFilingRepository taxFilingRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final BlockchainService blockchainService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    @Transactional
    public PaymentResponse processPayment(String userId, PaymentRequest request) {
        log.info("Processing payment for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TaxFiling taxFiling = taxFilingRepository.findById(request.getTaxFilingId())
                .orElseThrow(() -> new ResourceNotFoundException("Tax filing not found"));

        if (!taxFiling.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        // Generate transaction reference
        String transactionReference = "TXN" + System.currentTimeMillis();

        // Create payment record
        Payment payment = Payment.builder()
                .user(user)
                .taxFiling(taxFiling)
                .amount(request.getAmount())
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .status(Payment.PaymentStatus.PENDING)
                .transactionReference(transactionReference)
                .build();

        payment = paymentRepository.save(payment);

        // Prepare payment details
        Map<String, String> paymentDetails = new HashMap<>();
        paymentDetails.put("phoneNumber", request.getPhoneNumber());
        paymentDetails.put("bankName", request.getBankName());
        paymentDetails.put("accountNumber", request.getAccountNumber());

        // Process payment through gateway
        Map<String, Object> gatewayResponse = paymentGatewayService.processPayment(payment, paymentDetails);

        if ((Boolean) gatewayResponse.get("success")) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            payment.setExternalTransactionId((String) gatewayResponse.get("transactionId"));

            // Record on blockchain
            String txHash = blockchainService.recordPayment(payment);
            payment.setBlockchainTxHash(txHash);

            // Send notification
            notificationService.sendPaymentConfirmation(user, payment);

            log.info("Payment processed successfully: {}", payment.getId());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason((String) gatewayResponse.get("message"));
            log.error("Payment processing failed: {}", gatewayResponse.get("message"));
        }

        payment = paymentRepository.save(payment);

        // Log action
        auditLogService.logAction(userId, "PAYMENT_PROCESSED",
                AuditLog.EntityType.SYSTEM, payment.getId(),
                "Payment " + payment.getStatus());

        return buildPaymentResponse(payment);
    }

    public List<PaymentResponse> getUserPayments(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Payment> payments = paymentRepository.findByUserOrderByCreatedAtDesc(user);

        return payments.stream()
                .map(this::buildPaymentResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentById(String userId, String paymentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (!payment.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        return buildPaymentResponse(payment);
    }

    private PaymentResponse buildPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .transactionReference(payment.getTransactionReference())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .taxFilingId(payment.getTaxFiling() != null ? payment.getTaxFiling().getId() : null)
                .blockchainTxHash(payment.getBlockchainTxHash())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}