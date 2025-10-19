package zm.zra.zra_digital_fortress_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vat_filings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VatFiling extends TaxFiling {

    @Column(nullable = false)
    private Double totalSales = 0.0;

    @Column(nullable = false)
    private Double taxableSales = 0.0;

    @Column(nullable = false)
    private Double zeroRatedSales = 0.0;

    @Column(nullable = false)
    private Double exemptSales = 0.0;

    @Column(nullable = false)
    private Double totalPurchases = 0.0;

    @Column(nullable = false)
    private Double taxablePurchases = 0.0;

    @Column(nullable = false)
    private Double zeroRatedPurchases = 0.0;

    @Column(nullable = false)
    private Double exemptPurchases = 0.0;

    @Column(nullable = false)
    private Double capitalGoodsPurchases = 0.0;

    @Column(nullable = false)
    private Double outputVat = 0.0;

    @Column(nullable = false)
    private Double inputVat = 0.0;

    @Column(nullable = false)
    private Double vatPayable = 0.0;

    @Column(nullable = false)
    private Double vatRate = 0.16; // 16% standard rate
}