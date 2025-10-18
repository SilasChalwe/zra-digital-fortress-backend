package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "income_tax_filings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IncomeTaxFiling extends TaxFiling {

    // Income Sources
    @Column(nullable = false)
    private Double employmentIncome = 0.0;

    @Column(nullable = false)
    private Double businessIncome = 0.0;

    @Column(nullable = false)
    private Double rentalIncome = 0.0;

    @Column(nullable = false)
    private Double investmentIncome = 0.0;

    @Column(nullable = false)
    private Double otherIncome = 0.0;

    // Deductions
    @Column(nullable = false)
    private Double nappsaContributions = 0.0;

    @Column(nullable = false)
    private Double medicalExpenses = 0.0;

    @Column(nullable = false)
    private Double educationExpenses = 0.0;

    @Column(nullable = false)
    private Double insurancePremiums = 0.0;

    @Column(nullable = false)
    private Double otherDeductions = 0.0;

    // Tax Calculations
    @Column(nullable = false)
    private Double taxBracket1Amount = 0.0; // 0%

    @Column(nullable = false)
    private Double taxBracket2Amount = 0.0; // 25%

    @Column(nullable = false)
    private Double taxBracket3Amount = 0.0; // 30%

    @Column(nullable = false)
    private Double calculatedTax = 0.0;

    @Column(nullable = false)
    private Double previousPayments = 0.0;

    @Column(nullable = false)
    private Double refundOrPayment = 0.0; // Negative for refund, positive for payment
}