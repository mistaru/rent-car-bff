package kg.founders.core.services.rental;

import kg.founders.core.model.rental.AddOnRequest;
import kg.founders.core.model.rental.PriceBreakdown;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface PricingService {
    @Transactional(readOnly = true)
    PriceBreakdown calculateForVehicle(Long vehicleId, int days, List<AddOnRequest> addOns, String currency);

    PriceBreakdown calculate(BigDecimal pricePerDay, int days, List<AddOnRequest> addOns, String currency);
}
