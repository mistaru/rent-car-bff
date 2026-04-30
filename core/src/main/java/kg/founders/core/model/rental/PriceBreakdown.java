package kg.founders.core.model.rental;

import kg.founders.core.enums.PricingType;
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
    /** Описание тарифного диапазона, например «Тариф 4–7 дней» */
    private final String tierName;
    private final List<AddOnPriceItem> addOnItems;
    private final BigDecimal addOnsAmount;
    private final BigDecimal serviceFee;
    private final BigDecimal totalAmount;
    /** Предоплата 15% от totalAmount */
    private final BigDecimal prepaymentAmount;
    private final String currency;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class AddOnPriceItem {
        private final String name;
        private final String code;
        private final BigDecimal pricePerDay;
        private final int quantity;
        private final BigDecimal total;
        private final PricingType pricingType;
    }
}
