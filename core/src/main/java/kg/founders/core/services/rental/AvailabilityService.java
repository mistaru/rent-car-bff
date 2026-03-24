package kg.founders.core.services.rental;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface AvailabilityService {
    @Transactional(readOnly = true)
    boolean isVehicleAvailable(Long vehicleId, LocalDate pickupDate, LocalDate dropoffDate);
}
