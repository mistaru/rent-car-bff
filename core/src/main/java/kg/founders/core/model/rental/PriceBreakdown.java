package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class PriceBreakdown {
    private final int days;
    private final BigDecimal pricePerDay;
    private final BigDecimal baseAmount;
    private final List<AddOnPriceItem> addOnItems;
    private final BigDecimal addOnsAmount;
    private final BigDecimal serviceFee;
    private final BigDecimal totalAmount;
    private final String currency;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class AddOnPriceItem {
        private final String name;
        private final BigDecimal pricePerDay;
        private final BigDecimal total;
    }
}
