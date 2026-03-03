package kg.founders.core.services.rental;

import kg.founders.core.entity.Vehicle;
import kg.founders.core.enums.BookingStatus;
import kg.founders.core.enums.VehicleStatus;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.repo.BookingRepository;
import kg.founders.core.repo.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    public boolean isVehicleAvailable(Long vehicleId, LocalDate pickupDate, LocalDate dropoffDate) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));

        if (vehicle.getStatus() == VehicleStatus.BOOKED || vehicle.getStatus() == VehicleStatus.UNAVAILABLE) {
            log.info("Vehicle {} is not available, status: {}", vehicleId, vehicle.getStatus());
            return false;
        }

        boolean hasOverlap = bookingRepository.existsOverlappingBooking(
                vehicleId,
                pickupDate,
                dropoffDate,
                Arrays.asList(BookingStatus.CANCELLED)
        );

        if (hasOverlap) {
            log.info("Vehicle {} has overlapping booking for dates {} - {}", vehicleId, pickupDate, dropoffDate);
            return false;
        }

        return true;
    }
}
