package kg.founders.core.services.rental;

import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.enums.BookingStatus;
import kg.founders.core.enums.VehicleStatus;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.repo.BookingRepository;
import kg.founders.core.repo.VehicleBlockedPeriodRepository;
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
    private final VehicleBlockedPeriodRepository blockedPeriodRepository;

    @Transactional(readOnly = true)
    public boolean isVehicleAvailable(Long vehicleId, LocalDate pickupDate, LocalDate dropoffDate) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));

        if (vehicle.getStatus() == VehicleStatus.BOOKED || vehicle.getStatus() == VehicleStatus.UNAVAILABLE) {
            log.info("Vehicle {} is not available, status: {}", vehicleId, vehicle.getStatus());
            return false;
        }

        // Учитываем сервисный день: блокируем 1 день до начала и 1 день после окончания
        LocalDate blockStart = pickupDate.minusDays(1);
        LocalDate blockEnd = dropoffDate.plusDays(1);

        boolean hasOverlap = bookingRepository.existsOverlappingBooking(
                vehicleId,
                blockStart,
                blockEnd,
                Arrays.asList(BookingStatus.CANCELLED)
        );

        if (hasOverlap) {
            log.info("Vehicle {} has overlapping booking (including service days) for dates {} - {}",
                    vehicleId, pickupDate, dropoffDate);
            return false;
        }

        // Проверяем ручные блокировки (техобслуживание, ремонт и т.д.)
        boolean hasBlockedPeriod = blockedPeriodRepository.existsOverlappingBlock(
                vehicleId, pickupDate, dropoffDate);

        if (hasBlockedPeriod) {
            log.info("Vehicle {} has a manual blocked period for dates {} - {}",
                    vehicleId, pickupDate, dropoffDate);
            return false;
        }

        return true;
    }
}
