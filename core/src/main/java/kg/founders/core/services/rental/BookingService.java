package kg.founders.core.services.rental;

import kg.founders.core.converter.RentalMapper;
import kg.founders.core.entity.*;
import kg.founders.core.enums.*;
import kg.founders.core.exceptions.*;
import kg.founders.core.model.rental.*;
import kg.founders.core.repo.*;
import kg.founders.core.services.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;
    private final AvailabilityService availabilityService;
    private final PricingService pricingService;
    private final RentalMapper rentalMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public BookingDto createBooking(CreateBookingRequest request) {
        log.info("Creating booking for vehicle {} by customer {}", request.getVehicleId(), request.getCustomerId());

        validateDates(request.getPickupDate(), request.getDropoffDate());

        try {
            return doCreateBooking(request);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic locking conflict for vehicle {}, retrying...", request.getVehicleId());
            throw new BookingConflictException(
                    "Vehicle is being booked by another user. Please try again.");
        }
    }

    private BookingDto doCreateBooking(CreateBookingRequest request) {
        // 1. Find and lock vehicle
        Vehicle vehicle = vehicleRepository.findByIdWithLock(request.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + request.getVehicleId()));

        // 2. Check availability
        if (!availabilityService.isVehicleAvailable(
                vehicle.getId(), request.getPickupDate(), request.getDropoffDate())) {
            throw new VehicleNotAvailableException(
                    "Vehicle " + vehicle.getId() + " is not available for the requested dates");
        }

        // 3. Load related entities
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + request.getCustomerId()));
        Location pickupLocation = locationRepository.findById(request.getPickupLocationId())
                .orElseThrow(() -> new NotFoundException("Pickup location not found with id: " + request.getPickupLocationId()));
        Location dropoffLocation = locationRepository.findById(request.getDropoffLocationId())
                .orElseThrow(() -> new NotFoundException("Dropoff location not found with id: " + request.getDropoffLocationId()));

        // 4. Calculate days and pricing
        int days = (int) ChronoUnit.DAYS.between(request.getPickupDate(), request.getDropoffDate());
        String currency = request.getCurrency() != null ? request.getCurrency() : "USD";

        PriceBreakdown price = pricingService.calculate(
                vehicle.getPricePerDay(), days, request.getAddOns(), currency);

        // 5. Determine booking status based on payment method
        BookingStatus bookingStatus;
        PaymentStatus paymentStatus;

        if (request.getPaymentMethod() == PaymentMethod.ONLINE) {
            bookingStatus = BookingStatus.PENDING_PAYMENT;
            paymentStatus = PaymentStatus.UNPAID;
        } else {
            bookingStatus = BookingStatus.CONFIRMED;
            paymentStatus = PaymentStatus.UNPAID;
        }

        // 6. Build booking
        Booking booking = Booking.builder()
                .vehicle(vehicle)
                .customer(customer)
                .pickupLocation(pickupLocation)
                .dropoffLocation(dropoffLocation)
                .pickupDate(request.getPickupDate())
                .dropoffDate(request.getDropoffDate())
                .days(days)
                .baseAmount(price.getBaseAmount())
                .addOnsAmount(price.getAddOnsAmount())
                .serviceFee(price.getServiceFee())
                .totalAmount(price.getTotalAmount())
                .currency(currency)
                .status(bookingStatus)
                .paymentStatus(paymentStatus)
                .addOns(new ArrayList<>())
                .build();

        // 7. Add add-ons
        if (request.getAddOns() != null) {
            for (AddOnType addOnType : request.getAddOns()) {
                BookingAddOn addOn = BookingAddOn.builder()
                        .booking(booking)
                        .addOnType(addOnType)
                        .pricePerDay(addOnType.getPricePerDay())
                        .build();
                booking.getAddOns().add(addOn);
            }
        }

        // 8. Set vehicle to RESERVED
        vehicle.setStatus(VehicleStatus.RESERVED);
        vehicleRepository.save(vehicle);

        // 9. Save booking
        booking = bookingRepository.save(booking);
        log.info("Booking created with id: {}, status: {}", booking.getId(), bookingStatus);

        // 10. Publish event
        eventPublisher.publishEvent(
                new BookingCreatedEvent(this, booking.getId(), vehicle.getId(), customer.getId()));

        return rentalMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + id));
        return rentalMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerIdWithDetails(customerId).stream()
                .map(rentalMapper::toBookingDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public BookingDto cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Vehicle vehicle = booking.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        booking = bookingRepository.save(booking);
        log.info("Booking {} cancelled", bookingId);

        return rentalMapper.toBookingDto(booking);
    }

    private void validateDates(LocalDate pickupDate, LocalDate dropoffDate) {
        if (!dropoffDate.isAfter(pickupDate)) {
            throw new BadRequestException("Dropoff date must be after pickup date");
        }
        long days = ChronoUnit.DAYS.between(pickupDate, dropoffDate);
        if (days < 1) {
            throw new BadRequestException("Minimum rental period is 1 day");
        }
    }
}
