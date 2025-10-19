package zm.zra.zra_digital_fortress_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zm.zra.zra_digital_fortress_backend.dto.request.TaxFilingRequest;
import zm.zra.zra_digital_fortress_backend.dto.response.TaxFilingResponse;
import zm.zra.zra_digital_fortress_backend.exception.BadRequestException;
import zm.zra.zra_digital_fortress_backend.exception.ResourceNotFoundException;
import zm.zra.zra_digital_fortress_backend.integration.AiServiceClient;
import zm.zra.zra_digital_fortress_backend.integration.BlockchainService;
import zm.zra.zra_digital_fortress_backend.model.*;
import zm.zra.zra_digital_fortress_backend.repository.TaxFilingRepository;
import zm.zra.zra_digital_fortress_backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxFilingService {

    private final TaxFilingRepository taxFilingRepository;
    private final UserRepository userRepository;
    private final TaxCalculationService taxCalculationService;
    private final AiServiceClient aiServiceClient;
    private final BlockchainService blockchainService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final ComplianceService complianceService;
    private final ObjectMapper objectMapper;

    @Transactional
    public TaxFilingResponse submitTaxFiling(String userId, TaxFilingRequest request) {
        log.info("Processing tax filing for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check for duplicate filing
        taxFilingRepository.findByUserAndTaxYearAndTaxPeriodAndTaxType(
                user, 
                request.getTaxYear(), 
                request.getTaxPeriod(),
                TaxFiling.TaxType.valueOf(request.getTaxType())
        ).ifPresent(existing -> {
            if (!existing.getStatus().equals(TaxFiling.FilingStatus.DRAFT)) {
                throw new BadRequestException("Tax filing already exists for this period");
            }
        });

        // Calculate tax
        Map<String, Object> taxCalculation = taxCalculationService.calculateIncomeTax(request);

        // Create Income Tax Filing
        IncomeTaxFiling filing = new IncomeTaxFiling();
        filing.setUser(user);
        filing.setTaxType(TaxFiling.TaxType.INCOME_TAX);
        filing.setTaxYear(request.getTaxYear());
        filing.setTaxPeriod(request.getTaxPeriod());
        filing.setStatus(request.getSaveDraft() ? 
                TaxFiling.FilingStatus.DRAFT : TaxFiling.FilingStatus.SUBMITTED);

        // Set income sources
        filing.setEmploymentIncome(request.getEmploymentIncome());
        filing.setBusinessIncome(request.getBusinessIncome());
        filing.setRentalIncome(request.getRentalIncome());
        filing.setInvestmentIncome(request.getInvestmentIncome());
        filing.setOtherIncome(request.getOtherIncome());

        // Set deductions
        filing.setNappsaContributions(request.getNappsaContributions());
        filing.setMedicalExpenses(request.getMedicalExpenses());
        filing.setEducationExpenses(request.getEducationExpenses());
        filing.setInsurancePremiums(request.getInsurancePremiums());
        filing.setOtherDeductions(request.getOtherDeductions());

        // Set calculated values
        filing.setTotalIncome((Double) taxCalculation.get("totalIncome"));
        filing.setTotalDeductions((Double) taxCalculation.get("totalDeductions"));
        filing.setTaxableIncome((Double) taxCalculation.get("taxableIncome"));
        filing.setTaxDue((Double) taxCalculation.get("totalTax"));

        // Set tax brackets
        filing.setTaxBracket1Amount((Double) taxCalculation.get("bracket1Amount"));
        filing.setTaxBracket2Amount((Double) taxCalculation.get("bracket2Amount"));
        filing.setTaxBracket3Amount((Double) taxCalculation.get("bracket3Amount"));
        filing.setCalculatedTax((Double) taxCalculation.get("totalTax"));

        // Store complete filing data as JSON
        try {
            filing.setFilingData(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error("Error serializing filing data", e);
        }

        if (!request.getSaveDraft()) {
            filing.setSubmittedAt(LocalDateTime.now());

            // AI Risk Assessment
            Map<String, Object> riskAssessment = aiServiceClient.assessFraudRisk(filing);
            filing.setRiskScore((Double) riskAssessment.get("riskScore"));
            
            // FIX: Handle JsonProcessingException for risk factors
            try {
                filing.setRiskFactors(objectMapper.writeValueAsString(riskAssessment.get("factors")));
            } catch (JsonProcessingException e) {
                log.error("Error serializing risk factors", e);
            }

            // Record on blockchain
            String txHash = blockchainService.recordTaxFiling(filing);
            filing.setBlockchainTxHash(txHash);

            // Update compliance score
            complianceService.updateComplianceScore(user, filing);

            // Send notification
            notificationService.sendFilingConfirmation(user, filing);
        }

        filing = taxFilingRepository.save(filing);

        // Log action
        auditLogService.logAction(userId, "TAX_FILING_SUBMITTED", 
                AuditLog.EntityType.TAX_FILING, filing.getId(), 
                "Tax filing " + (request.getSaveDraft() ? "saved as draft" : "submitted"));

        log.info("Tax filing completed: {}", filing.getId());

        return buildTaxFilingResponse(filing, taxCalculation);
    }

    public List<TaxFilingResponse> getUserFilings(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<TaxFiling> filings = taxFilingRepository.findByUserOrderByCreatedAtDesc(user);

        return filings.stream()
                .map(this::buildTaxFilingResponse)
                .collect(Collectors.toList());
    }

    public TaxFilingResponse getFilingById(String userId, String filingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TaxFiling filing = taxFilingRepository.findById(filingId)
                .orElseThrow(() -> new ResourceNotFoundException("Tax filing not found"));

        if (!filing.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        return buildTaxFilingResponse(filing);
    }

    private TaxFilingResponse buildTaxFilingResponse(TaxFiling filing) {
        return buildTaxFilingResponse(filing, null);
    }

    private TaxFilingResponse buildTaxFilingResponse(TaxFiling filing, Map<String, Object> taxCalc) {
        TaxFilingResponse response = TaxFilingResponse.builder()
                .id(filing.getId())
                .taxType(filing.getTaxType().name())
                .taxYear(filing.getTaxYear())
                .taxPeriod(filing.getTaxPeriod())
                .status(filing.getStatus().name())
                .totalIncome(filing.getTotalIncome())
                .totalDeductions(filing.getTotalDeductions())
                .taxableIncome(filing.getTaxableIncome())
                .taxDue(filing.getTaxDue())
                .riskScore(filing.getRiskScore())
                .blockchainTxHash(filing.getBlockchainTxHash())
                .submittedAt(filing.getSubmittedAt())
                .createdAt(filing.getCreatedAt())
                .build();

        if (taxCalc != null) {
            response.setEffectiveTaxRate((Double) taxCalc.get("effectiveTaxRate"));
        }

        return response;
    }
}