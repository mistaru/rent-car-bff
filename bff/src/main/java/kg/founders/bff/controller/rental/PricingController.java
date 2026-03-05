package kg.founders.bff.controller.rental;

import kg.founders.core.enums.AddOnType;
import kg.founders.core.model.rental.PriceBreakdown;
import kg.founders.core.services.rental.PricingService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    /**
     * Расчёт стоимости по vehicleId (динамическое ценообразование через PricingTemplate).
     * Фронт должен использовать этот endpoint.
     */
    @ManualPermissionControl
    @GetMapping("/calculate/vehicle/{vehicleId}")
    public ResponseEntity<PriceBreakdown> calculateForVehicle(
            @PathVariable Long vehicleId,
            @RequestParam int days,
            @RequestParam(required = false) List<AddOnType> addOns,
            @RequestParam(defaultValue = "USD") String currency) {
        return ResponseEntity.ok(pricingService.calculateForVehicle(vehicleId, days, addOns, currency));
    }

    /**
     * Старый endpoint — расчёт по pricePerDay напрямую (обратная совместимость).
     */
    @ManualPermissionControl
    @GetMapping("/calculate")
    public ResponseEntity<PriceBreakdown> calculatePrice(
            @RequestParam BigDecimal pricePerDay,
            @RequestParam int days,
            @RequestParam(required = false) List<AddOnType> addOns,
            @RequestParam(defaultValue = "USD") String currency) {
        return ResponseEntity.ok(pricingService.calculate(pricePerDay, days, addOns, currency));
    }
}
