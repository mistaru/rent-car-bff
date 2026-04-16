package kg.founders.core.services.rental.impl;

import kg.founders.core.entity.rental.PriceTier;
import kg.founders.core.entity.rental.PricingTemplate;
import kg.founders.core.entity.rental.ServiceOption;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.enums.AddOnType;
import kg.founders.core.exceptions.BadRequestException;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.AddOnRequest;
import kg.founders.core.model.rental.PriceBreakdown;
import kg.founders.core.repo.PricingTemplateRepository;
import kg.founders.core.repo.ServiceOptionRepository;
import kg.founders.core.repo.VehicleRepository;
import kg.founders.core.services.rental.PricingService;
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
public class PricingServiceImpl implements PricingService {

    private static final BigDecimal SERVICE_FEE_RATE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal PREPAYMENT_RATE = new BigDecimal("0.15");  // 15%

    private final VehicleRepository vehicleRepository;
    private final PricingTemplateRepository pricingTemplateRepository;
    private final ServiceOptionRepository serviceOptionRepository;

    /**
     * Рассчитывает стоимость аренды для конкретного автомобиля,
     * используя динамическое ценообразование через PricingTemplate.
     * Если у машины нет шаблона — fallback на vehicle.pricePerDay.
     */
    @Transactional(readOnly = true)
    @Override
    public PriceBreakdown calculateForVehicle(Long vehicleId, int days, List<AddOnRequest> addOns, String currency) {
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

        return calculateByCodes(pricePerDay, days, addOns, currency != null ? currency : "USD", tierName);
    }

    /**
     * Базовый расчёт — совместимость со старым API (без vehicleId).
     */
    @Override
    public PriceBreakdown calculate(BigDecimal pricePerDay, int days, List<AddOnRequest> addOns, String currency) {
        return calculateByCodes(pricePerDay, days, addOns, currency, null);
    }

    /**
     * Полный расчёт стоимости с детализацией.
     * Цена доп. услуг берётся из таблицы service_options по коду.
     * Если не найдена — fallback на AddOnType enum (обратная совместимость).
     * Количество учитывается через AddOnRequest.quantity.
     */
    private PriceBreakdown calculateByCodes(BigDecimal pricePerDay, int days, List<AddOnRequest> addOns,
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
            for (AddOnRequest req : addOns) {
                String code = req.getCode();
                int qty = req.getEffectiveQuantity();
                BigDecimal addOnPricePerDay;
                String displayName;

                // Сначала ищем в таблице service_options
                ServiceOption option = serviceOptionRepository.findByCode(code).orElse(null);
                if (option != null) {
                    addOnPricePerDay = option.getPricePerDay();
                    displayName = option.getName();
                } else {
                    // Fallback: ищем в enum AddOnType (обратная совместимость)
                    try {
                        AddOnType enumVal = AddOnType.valueOf(code);
                        addOnPricePerDay = enumVal.getPricePerDay();
                        displayName = code;
                    } catch (IllegalArgumentException e) {
                        log.warn("Unknown add-on code: {}, skipping", code);
                        continue;
                    }
                }

                BigDecimal addOnTotal = addOnPricePerDay
                        .multiply(BigDecimal.valueOf(qty))
                        .multiply(BigDecimal.valueOf(days))
                        .setScale(2, RoundingMode.HALF_UP);
                addOnItems.add(PriceBreakdown.AddOnPriceItem.builder()
                        .name(displayName)
                        .code(code)
                        .pricePerDay(addOnPricePerDay)
                        .quantity(qty)
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
    private PriceTier findMatchingTier(PricingTemplate template, int days) {
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
