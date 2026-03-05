package kg.founders.core.services.rental;

import kg.founders.core.entity.rental.PriceTier;
import kg.founders.core.entity.rental.PricingTemplate;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.enums.AddOnType;
import kg.founders.core.exceptions.BadRequestException;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.PriceBreakdown;
import kg.founders.core.repo.PricingTemplateRepository;
import kg.founders.core.repo.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingService {

    private static final BigDecimal SERVICE_FEE_RATE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal PREPAYMENT_RATE = new BigDecimal("0.15");  // 15%

    private final VehicleRepository vehicleRepository;
    private final PricingTemplateRepository pricingTemplateRepository;

    /**
     * Рассчитывает стоимость аренды для конкретного автомобиля,
     * используя динамическое ценообразование через PricingTemplate.
     * Если у машины нет шаблона — fallback на vehicle.pricePerDay.
     */
    @Transactional(readOnly = true)
    public PriceBreakdown calculateForVehicle(Long vehicleId, int days, List<AddOnType> addOns, String currency) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));

        BigDecimal pricePerDay;
        String tierName = null;

        if (vehicle.getPricingTemplate() != null) {
            PricingTemplate template = pricingTemplateRepository.findByIdWithTiers(vehicle.getPricingTemplate().getId())
                    .orElseThrow(() -> new NotFoundException("Pricing template not found"));

            PriceTier matchedTier = findMatchingTier(template, days);
            pricePerDay = matchedTier.getPricePerDay();
            tierName = formatTierName(matchedTier);
        } else {
            pricePerDay = vehicle.getPricePerDay();
        }

        return calculate(pricePerDay, days, addOns, currency != null ? currency : "USD", tierName);
    }

    /**
     * Базовый расчёт — совместимость со старым API (без vehicleId).
     * Использует переданную pricePerDay напрямую.
     */
    public PriceBreakdown calculate(BigDecimal pricePerDay, int days, List<AddOnType> addOns, String currency) {
        return calculate(pricePerDay, days, addOns, currency, null);
    }

    /**
     * Полный расчёт стоимости с детализацией.
     */
    public PriceBreakdown calculate(BigDecimal pricePerDay, int days, List<AddOnType> addOns,
                                     String currency, String tierName) {
        log.debug("Calculating price: pricePerDay={}, days={}, addOns={}, tier={}",
                pricePerDay, days, addOns, tierName);

        if (days < 1) {
            throw new BadRequestException("Rental period must be at least 1 day");
        }

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
        BigDecimal prepaymentAmount = totalAmount.multiply(PREPAYMENT_RATE).setScale(2, RoundingMode.HALF_UP);

        return PriceBreakdown.builder()
                .days(days)
                .pricePerDay(pricePerDay)
                .baseAmount(baseAmount)
                .tierName(tierName)
                .addOnItems(addOnItems)
                .addOnsAmount(addOnsAmount)
                .serviceFee(serviceFee)
                .totalAmount(totalAmount)
                .prepaymentAmount(prepaymentAmount)
                .currency(currency != null ? currency : "USD")
                .build();
    }

    /**
     * Находит подходящий тарифный диапазон по количеству дней.
     */
    public PriceTier findMatchingTier(PricingTemplate template, int days) {
        return template.getTiers().stream()
                .filter(tier -> days >= tier.getMinDays()
                        && (tier.getMaxDays() == null || days <= tier.getMaxDays()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "No matching price tier found for " + days + " days in template '" + template.getName() + "'"));
    }

    /**
     * Форматирует описание тарифного диапазона, например «Тариф 4–7 дней» или «Тариф от 15 дней».
     */
    private String formatTierName(PriceTier tier) {
        if (tier.getMaxDays() == null) {
            return "Тариф от " + tier.getMinDays() + " дней";
        }
        if (tier.getMinDays().equals(tier.getMaxDays())) {
            return "Тариф " + tier.getMinDays() + " день";
        }
        return "Тариф " + tier.getMinDays() + "–" + tier.getMaxDays() + " дней";
    }
}
