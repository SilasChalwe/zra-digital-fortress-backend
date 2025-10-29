package zm.zra.zra_digital_fortress_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxFilingRequest {

    @NotBlank(message = "Tax type is required")
    private String taxType; // INCOME_TAX, VAT, COMPANY_TAX

    @NotNull(message = "Tax year is required")
    @Min(value = 2000, message = "Tax year must be 2000 or later")
    @Max(value = 2100, message = "Tax year must be before 2100")
    private Integer taxYear;

    @NotNull(message = "Tax period is required")
    @Min(value = 1)
    @Max(value = 12)
    private Integer taxPeriod;

    // Income Sources
    @NotNull
    @PositiveOrZero(message = "Employment income must be zero or positive")
    private Double employmentIncome = 0.0;

    @NotNull
    @PositiveOrZero
    private Double businessIncome = 0.0;

    @NotNull
    @PositiveOrZero
    private Double rentalIncome = 0.0;

    @NotNull
    @PositiveOrZero
    private Double investmentIncome = 0.0;

    @NotNull
    @PositiveOrZero
    private Double otherIncome = 0.0;

    // Deductions
    @NotNull
    @PositiveOrZero
    private Double nappsaContributions = 0.0;

    @NotNull
    @PositiveOrZero
    private Double medicalExpenses = 0.0;

    @NotNull
    @PositiveOrZero
    private Double educationExpenses = 0.0;

    @NotNull
    @PositiveOrZero
    private Double insurancePremiums = 0.0;

    @NotNull
    @PositiveOrZero
    private Double otherDeductions = 0.0;

    private Boolean saveDraft = false;
    
    @NotNull
@PositiveOrZero
private Double riskScore = 0.0;
}