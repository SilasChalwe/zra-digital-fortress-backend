package zm.zra.zra_digital_fortress_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zm.zra.zra_digital_fortress_backend.dto.request.TaxFilingRequest;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxCalculationService {

    // Zambian Tax Brackets (Annual amounts in ZMW)
    private static final double BRACKET_1_LIMIT = 57600.0;  // 0% (4,800 * 12)
    private static final double BRACKET_2_LIMIT = 86400.0;  // 25% (7,200 * 12)
    // Above 86,400 = 30%

    private static final double BRACKET_1_RATE = 0.0;
    private static final double BRACKET_2_RATE = 0.25;
    private static final double BRACKET_3_RATE = 0.30;

    private static final double MAX_NAPPSA_DEDUCTION_RATE = 0.10; // 10% of income

    public Map<String, Object> calculateIncomeTax(TaxFilingRequest request) {
        log.info("Calculating income tax for tax year {}", request.getTaxYear());

        // Calculate total income
        double totalIncome = request.getEmploymentIncome() +
                request.getBusinessIncome() +
                request.getRentalIncome() +
                request.getInvestmentIncome() +
                request.getOtherIncome();

        // Validate and calculate deductions
        double validatedNappsa = Math.min(request.getNappsaContributions(), 
                totalIncome * MAX_NAPPSA_DEDUCTION_RATE);
        
        double totalDeductions = validatedNappsa +
                request.getMedicalExpenses() +
                request.getEducationExpenses() +
                request.getInsurancePremiums() +
                request.getOtherDeductions();

        // Calculate taxable income
        double taxableIncome = Math.max(0, totalIncome - totalDeductions);

        // Calculate tax using brackets
        Map<String, Double> bracketCalculations = calculateTaxByBrackets(taxableIncome);

        double totalTax = bracketCalculations.get("bracket1Tax") +
                bracketCalculations.get("bracket2Tax") +
                bracketCalculations.get("bracket3Tax");

        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("totalDeductions", totalDeductions);
        result.put("taxableIncome", taxableIncome);
        result.put("bracket1Amount", bracketCalculations.get("bracket1Amount"));
        result.put("bracket2Amount", bracketCalculations.get("bracket2Amount"));
        result.put("bracket3Amount", bracketCalculations.get("bracket3Amount"));
        result.put("bracket1Tax", bracketCalculations.get("bracket1Tax"));
        result.put("bracket2Tax", bracketCalculations.get("bracket2Tax"));
        result.put("bracket3Tax", bracketCalculations.get("bracket3Tax"));
        result.put("totalTax", totalTax);
        result.put("effectiveTaxRate", taxableIncome > 0 ? (totalTax / taxableIncome) * 100 : 0);

        log.info("Tax calculation completed. Total tax: {}", totalTax);

        return result;
    }

    private Map<String, Double> calculateTaxByBrackets(double taxableIncome) {
        Map<String, Double> brackets = new HashMap<>();

        double bracket1Amount = 0.0;
        double bracket2Amount = 0.0;
        double bracket3Amount = 0.0;

        if (taxableIncome <= BRACKET_1_LIMIT) {
            // All income in first bracket (0% tax)
            bracket1Amount = taxableIncome;
        } else if (taxableIncome <= BRACKET_2_LIMIT) {
            // Income spans first two brackets
            bracket1Amount = BRACKET_1_LIMIT;
            bracket2Amount = taxableIncome - BRACKET_1_LIMIT;
        } else {
            // Income spans all three brackets
            bracket1Amount = BRACKET_1_LIMIT;
            bracket2Amount = BRACKET_2_LIMIT - BRACKET_1_LIMIT;
            bracket3Amount = taxableIncome - BRACKET_2_LIMIT;
        }

        brackets.put("bracket1Amount", bracket1Amount);
        brackets.put("bracket2Amount", bracket2Amount);
        brackets.put("bracket3Amount", bracket3Amount);
        brackets.put("bracket1Tax", bracket1Amount * BRACKET_1_RATE);
        brackets.put("bracket2Tax", bracket2Amount * BRACKET_2_RATE);
        brackets.put("bracket3Tax", bracket3Amount * BRACKET_3_RATE);

        return brackets;
    }

    public Map<String, Object> calculateVAT(double sales, double purchases, double vatRate) {
        double outputVAT = sales * vatRate;
        double inputVAT = purchases * vatRate;
        double vatPayable = Math.max(0, outputVAT - inputVAT);

        Map<String, Object> result = new HashMap<>();
        result.put("sales", sales);
        result.put("purchases", purchases);
        result.put("outputVAT", outputVAT);
        result.put("inputVAT", inputVAT);
        result.put("vatPayable", vatPayable);

        return result;
    }

    public double calculatePenalty(double taxDue, int daysLate) {
        // 10% initial penalty + 5% per month
        double initialPenalty = taxDue * 0.10;
        int monthsLate = (daysLate / 30);
        double monthlyPenalty = taxDue * 0.05 * monthsLate;
        
        return initialPenalty + monthlyPenalty;
    }

    public double calculateInterest(double amount, int daysLate) {
        // 5% per month interest
        double monthlyRate = 0.05;
        int months = (daysLate / 30);
        return amount * monthlyRate * months;
    }
}