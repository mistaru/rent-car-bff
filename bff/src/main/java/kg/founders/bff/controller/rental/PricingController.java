package kg.founders.bff.controller.rental;

import kg.founders.core.model.rental.AddOnRequest;
import kg.founders.core.model.rental.PriceBreakdown;
import kg.founders.core.services.rental.PricingService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    /**
     * Расчёт стоимости по vehicleId (динамическое ценообразование через PricingTemplate).
     * addOns передаются как коды: ?addOns=ROOF_TENT&addOns=SLEEPING_BAGS
     * qty передаётся параллельным массивом: ?qty=1&qty=2
     */
    @ManualPermissionControl
    @GetMapping("/calculate/vehicle/{vehicleId}")
    public ResponseEntity<PriceBreakdown> calculateForVehicle(
            @PathVariable Long vehicleId,
            @RequestParam int days,
            @RequestParam(required = false) List<String> addOns,
            @RequestParam(required = false) List<Integer> qty,
            @RequestParam(defaultValue = "USD") String currency) {
        List<AddOnRequest> addOnRequests = toAddOnRequests(addOns, qty);
        return ResponseEntity.ok(pricingService.calculateForVehicle(vehicleId, days, addOnRequests, currency));
    }

    /**
     * Старый endpoint — расчёт по pricePerDay напрямую (обратная совместимость).
     */
    @ManualPermissionControl
    @GetMapping("/calculate")
    public ResponseEntity<PriceBreakdown> calculatePrice(
            @RequestParam BigDecimal pricePerDay,
            @RequestParam int days,
            @RequestParam(required = false) List<String> addOns,
            @RequestParam(required = false) List<Integer> qty,
            @RequestParam(defaultValue = "USD") String currency) {
        List<AddOnRequest> addOnRequests = toAddOnRequests(addOns, qty);
        return ResponseEntity.ok(pricingService.calculate(pricePerDay, days, addOnRequests, currency));
    }

    private List<AddOnRequest> toAddOnRequests(List<String> addOns, List<Integer> qty) {
        if (addOns == null || addOns.isEmpty()) return null;
        List<AddOnRequest> result = new java.util.ArrayList<>();
        for (int i = 0; i < addOns.size(); i++) {
            int q = (qty != null && i < qty.size() && qty.get(i) != null) ? qty.get(i) : 1;
            result.add(AddOnRequest.builder().code(addOns.get(i)).quantity(q).build());
        }
        return result;
    }
}
