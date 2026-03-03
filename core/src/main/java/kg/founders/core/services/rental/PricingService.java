package kg.founders.core.services.rental;

import kg.founders.core.enums.AddOnType;
import kg.founders.core.model.rental.PriceBreakdown;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PricingService {

    private static final BigDecimal SERVICE_FEE_RATE = new BigDecimal("0.10"); // 10%

    public PriceBreakdown calculate(BigDecimal pricePerDay, int days, List<AddOnType> addOns, String currency) {
        log.debug("Calculating price: pricePerDay={}, days={}, addOns={}", pricePerDay, days, addOns);

        BigDecimal baseAmount = pricePerDay.multiply(BigDecimal.valueOf(days)).setScale(2, RoundingMode.HALF_UP);

        List<PriceBreakdown.AddOnPriceItem> addOnItems = new ArrayList<>();
        BigDecimal addOnsAmount = BigDecimal.ZERO;

        if (addOns != null) {
            for (AddOnType addOn : addOns) {
                BigDecimal addOnTotal = addOn.getPricePerDay()
                        .multiply(BigDecimal.valueOf(days))
                        .setScale(2, RoundingMode.HALF_UP);
                addOnItems.add(PriceBreakdown.AddOnPriceItem.builder()
                        .name(addOn.name())
                        .pricePerDay(addOn.getPricePerDay())
                        .total(addOnTotal)
                        .build());
                addOnsAmount = addOnsAmount.add(addOnTotal);
            }
        }

        BigDecimal subtotal = baseAmount.add(addOnsAmount);
        BigDecimal serviceFee = subtotal.multiply(SERVICE_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(serviceFee);

        return PriceBreakdown.builder()
                .days(days)
                .pricePerDay(pricePerDay)
                .baseAmount(baseAmount)
                .addOnItems(addOnItems)
                .addOnsAmount(addOnsAmount)
                .serviceFee(serviceFee)
                .totalAmount(totalAmount)
                .currency(currency != null ? currency : "USD")
                .build();
    }
}
