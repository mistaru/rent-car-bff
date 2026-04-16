package kg.founders.core.services.rental.impl;

import kg.founders.core.converter.rental.BookingConverter;
import kg.founders.core.entity.rental.*;
import kg.founders.core.enums.*;
import kg.founders.core.exceptions.*;
import kg.founders.core.model.rental.*;
import kg.founders.core.repo.*;
import kg.founders.core.services.event.BookingCreatedEvent;
import kg.founders.core.services.rental.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;
    private final AvailabilityService availabilityService;
    private final PricingService pricingService;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;
    private final BookingHistoryService bookingHistoryService;
    private final BookingEmailService bookingEmailService;
    private final ServiceOptionService serviceOptionService;
    private final BookingConverter bookingConverter;

    @Transactional
    @Override
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

        // 4. Calculate days and pricing (dynamic pricing via PricingTemplate)
        int days = (int) ChronoUnit.DAYS.between(request.getPickupDate(), request.getDropoffDate());
        String currency = request.getCurrency() != null ? request.getCurrency() : "USD";

        PriceBreakdown price = pricingService.calculateForVehicle(
                vehicle.getId(), days, request.getAddOns(), currency);

        // 5. Build booking with fixed pricing
        Booking booking = Booking.builder()
                .vehicle(vehicle)
                .customer(customer)
                .pickupLocation(pickupLocation)
                .dropoffLocation(dropoffLocation)
                .pickupDate(request.getPickupDate().minusDays(1)) //Service block: +1 day before for maintenance
                .dropoffDate(request.getDropoffDate().plusDays(1)) //Service block: +1 day after for maintenance
                .days(days)
                .pricePerDay(price.getPricePerDay())
                .priceTierDescription(price.getTierName())
                .baseAmount(price.getBaseAmount())
                .addOnsAmount(price.getAddOnsAmount())
                .serviceFee(price.getServiceFee())
                .totalAmount(price.getTotalAmount())
                .prepaymentAmount(price.getPrepaymentAmount())
                .prepaymentPaid(false)
                .currency(currency)
                .status(BookingStatus.PENDING_PAYMENT)
                .paymentStatus(PaymentStatus.UNPAID)
                .addOns(new ArrayList<>())
                .build();

        // 6. Validate inventory availability and add add-ons
        serviceOptionService.validateInventoryAvailability(request.getAddOns(), null);
        if (request.getAddOns() != null) {
            for (AddOnRequest addOnReq : request.getAddOns()) {
                AddOnType addOnType;
                try {
                    addOnType = AddOnType.valueOf(addOnReq.getCode());
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown add-on code: {}, skipping", addOnReq.getCode());
                    continue;
                }
                BookingAddOn addOn = BookingAddOn.builder()
                        .booking(booking)
                        .addOnType(addOnType)
                        .pricePerDay(addOnType.getPricePerDay())
                        .quantity(addOnReq.getEffectiveQuantity())
                        .build();
                booking.getAddOns().add(addOn);
            }
        }

        // 8. Set vehicle to RESERVED
        vehicle.setStatus(VehicleStatus.RESERVED);
        vehicleRepository.save(vehicle);

        // 9. Save booking
        booking = bookingRepository.save(booking);
        log.info("Booking created with id: {}, prepayment: {}",
                booking.getId(), price.getPrepaymentAmount());

        // 10. Send confirmation email
        bookingEmailService.sendBookingConfirmation(booking);

        // 11. Publish event
        eventPublisher.publishEvent(
                new BookingCreatedEvent(this, booking.getId(), vehicle.getId(), customer.getId()));

        // 12. Audit log
        bookingHistoryService.logCreated(booking, "system");

        return bookingConverter.convertFromEntity(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAllWithDetails().stream()
                .map(bookingConverter::convertFromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingCalendarItem> getBookingsForCalendar() {
        return bookingRepository.findAllWithDetails().stream()
                .map(b -> {
                    String color;
                    switch (b.getStatus()) {
                        case CONFIRMED: color = "#4CAF50"; break;
                        case PENDING_PAYMENT: color = "#FF9800"; break;
                        case CANCELLED: color = "#F44336"; break;
                        default: color = "#9E9E9E"; break;
                    }
                    return BookingCalendarItem.builder()
                            .id(b.getId())
                            .vehicleName(b.getVehicle().getBrand() + " " + b.getVehicle().getModel())
                            .vehicleImage(b.getVehicle().getImage())
                            .carClass(b.getVehicle().getCarClass())
                            .customerName(b.getCustomer().getFullName())
                            .pickupDate(b.getPickupDate().toString())
                            .dropoffDate(b.getDropoffDate().toString())
                            .days(b.getDays())
                            .status(b.getStatus().name())
                            .paymentStatus(b.getPaymentStatus().name())
                            .totalAmount(b.getTotalAmount())
                            .color(color)
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + id));
        return bookingConverter.convertFromEntity(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerIdWithDetails(customerId).stream()
                .map(bookingConverter::convertFromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    @Override
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

        paymentService.deletePaymentByBookingId(bookingId);

        booking = bookingRepository.save(booking);
        log.info("Booking {} cancelled", bookingId);

        bookingHistoryService.logCancelled(booking, "system");

        return bookingConverter.convertFromEntity(booking);
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long bookingId, UpdateBookingRequest request) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot update cancelled booking");
        }

        // Обновление дат
        boolean datesChanged = false;
        LocalDate newPickup = request.getPickupDate() != null ? request.getPickupDate() : booking.getPickupDate();
        LocalDate newDropoff = request.getDropoffDate() != null ? request.getDropoffDate() : booking.getDropoffDate();

        if (!newPickup.equals(booking.getPickupDate()) || !newDropoff.equals(booking.getDropoffDate())) {
            validateDates(newPickup, newDropoff);
            datesChanged = true;

            String oldDates = booking.getPickupDate() + " → " + booking.getDropoffDate();
            String newDates = newPickup + " → " + newDropoff;

            booking.setPickupDate(newPickup);
            booking.setDropoffDate(newDropoff);
            int days = (int) ChronoUnit.DAYS.between(newPickup, newDropoff);
            booking.setDays(days);

            // Пересчёт стоимости
            List<AddOnRequest> currentAddOns = booking.getAddOns() != null
                    ? booking.getAddOns().stream()
                        .map(a -> AddOnRequest.builder().code(a.getAddOnType().name()).quantity(a.getQuantity()).build())
                        .collect(java.util.stream.Collectors.toList())
                    : null;
            PriceBreakdown price = pricingService.calculateForVehicle(
                    booking.getVehicle().getId(), days, currentAddOns, booking.getCurrency());
            booking.setPricePerDay(price.getPricePerDay());
            booking.setPriceTierDescription(price.getTierName());
            booking.setBaseAmount(price.getBaseAmount());
            booking.setAddOnsAmount(price.getAddOnsAmount());
            booking.setServiceFee(price.getServiceFee());
            booking.setTotalAmount(price.getTotalAmount());
            booking.setPrepaymentAmount(price.getPrepaymentAmount());

            // Audit: dates changed
            bookingHistoryService.logFieldChange(booking, "DATES_CHANGED", "dates",
                    oldDates, newDates, "system");
        }

        // Обновление локаций
        if (request.getPickupLocationId() != null) {
            String oldLoc = booking.getPickupLocation() != null ? booking.getPickupLocation().getName() : null;
            Location pickupLoc = locationRepository.findById(request.getPickupLocationId())
                    .orElseThrow(() -> new NotFoundException("Pickup location not found"));
            booking.setPickupLocation(pickupLoc);
            if (oldLoc == null || !oldLoc.equals(pickupLoc.getName())) {
                bookingHistoryService.logFieldChange(booking, "UPDATED", "pickupLocation",
                        oldLoc, pickupLoc.getName(), "system");
            }
        }
        if (request.getDropoffLocationId() != null) {
            String oldLoc = booking.getDropoffLocation() != null ? booking.getDropoffLocation().getName() : null;
            Location dropoffLoc = locationRepository.findById(request.getDropoffLocationId())
                    .orElseThrow(() -> new NotFoundException("Dropoff location not found"));
            booking.setDropoffLocation(dropoffLoc);
            if (oldLoc == null || !oldLoc.equals(dropoffLoc.getName())) {
                bookingHistoryService.logFieldChange(booking, "UPDATED", "dropoffLocation",
                        oldLoc, dropoffLoc.getName(), "system");
            }
        }

        // Обновление статуса
        if (request.getStatus() != null) {
            try {
                BookingStatus oldStatus = booking.getStatus();
                BookingStatus newStatus = BookingStatus.valueOf(request.getStatus());
                if (oldStatus != newStatus) {
                    booking.setStatus(newStatus);
                    // Если подтверждаем — авто BOOKED, если отменяем — AVAILABLE
                    if (newStatus == BookingStatus.CONFIRMED) {
                        booking.getVehicle().setStatus(VehicleStatus.BOOKED);
                    } else if (newStatus == BookingStatus.CANCELLED) {
                        booking.getVehicle().setStatus(VehicleStatus.AVAILABLE);
                    }
                    bookingHistoryService.logFieldChange(booking, "STATUS_CHANGED", "status",
                            oldStatus.name(), newStatus.name(), "system");
                }
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid booking status: " + request.getStatus());
            }
        }

        // Обновление клиентских данных
        Customer customer = booking.getCustomer();
        boolean customerChanged = false;
        if (request.getCustomerFullName() != null && !request.getCustomerFullName().equals(customer.getFullName())) {
            String old = customer.getFullName();
            customer.setFullName(request.getCustomerFullName());
            bookingHistoryService.logFieldChange(booking, "CUSTOMER_UPDATED", "fullName", old, request.getCustomerFullName(), "system");
            customerChanged = true;
        }
        if (request.getCustomerEmail() != null && !request.getCustomerEmail().equals(customer.getEmail())) {
            String old = customer.getEmail();
            customer.setEmail(request.getCustomerEmail());
            bookingHistoryService.logFieldChange(booking, "CUSTOMER_UPDATED", "email", old, request.getCustomerEmail(), "system");
            customerChanged = true;
        }
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().equals(customer.getPhone())) {
            String old = customer.getPhone();
            customer.setPhone(request.getCustomerPhone());
            bookingHistoryService.logFieldChange(booking, "CUSTOMER_UPDATED", "phone", old, request.getCustomerPhone(), "system");
            customerChanged = true;
        }
        if (request.getCustomerAdditionalInfo() != null && !request.getCustomerAdditionalInfo().equals(customer.getAdditionalInfo())) {
            customer.setAdditionalInfo(request.getCustomerAdditionalInfo());
            customerChanged = true;
        }
        if (customerChanged) {
            customerRepository.save(customer);
        }

        // Обновление доп. услуг
        boolean addOnsChanged = false;
        if (request.getAddOns() != null) {
            // Валидация доступности инвентаря (исключаем текущее бронирование из подсчёта)
            serviceOptionService.validateInventoryAvailability(request.getAddOns(), bookingId);

            // Собираем старые для лога
            String oldAddOns = booking.getAddOns() != null
                    ? booking.getAddOns().stream()
                        .map(a -> a.getAddOnType().name() + "×" + a.getQuantity())
                        .collect(java.util.stream.Collectors.joining(", "))
                    : "";

            // Очищаем старые
            if (booking.getAddOns() != null) {
                booking.getAddOns().clear();
            }

            // Добавляем новые
            for (AddOnRequest addOnReq : request.getAddOns()) {
                AddOnType addOnType;
                try {
                    addOnType = AddOnType.valueOf(addOnReq.getCode());
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown add-on code during update: {}, skipping", addOnReq.getCode());
                    continue;
                }
                BookingAddOn addOn = BookingAddOn.builder()
                        .booking(booking)
                        .addOnType(addOnType)
                        .pricePerDay(addOnType.getPricePerDay())
                        .quantity(addOnReq.getEffectiveQuantity())
                        .build();
                booking.getAddOns().add(addOn);
            }

            String newAddOns = booking.getAddOns().stream()
                    .map(a -> a.getAddOnType().name() + "×" + a.getQuantity())
                    .collect(java.util.stream.Collectors.joining(", "));

            if (!oldAddOns.equals(newAddOns)) {
                addOnsChanged = true;
                bookingHistoryService.logFieldChange(booking, "UPDATED", "addOns",
                        oldAddOns, newAddOns, "system");
            }
        }

        // Пересчёт стоимости если изменились даты или доп. услуги
        if (!datesChanged && addOnsChanged) {
            List<AddOnRequest> currentAddOns = booking.getAddOns() != null
                    ? booking.getAddOns().stream()
                        .map(a -> AddOnRequest.builder().code(a.getAddOnType().name()).quantity(a.getQuantity()).build())
                        .collect(java.util.stream.Collectors.toList())
                    : null;
            PriceBreakdown price = pricingService.calculateForVehicle(
                    booking.getVehicle().getId(), booking.getDays(), currentAddOns, booking.getCurrency());
            booking.setPricePerDay(price.getPricePerDay());
            booking.setPriceTierDescription(price.getTierName());
            booking.setBaseAmount(price.getBaseAmount());
            booking.setAddOnsAmount(price.getAddOnsAmount());
            booking.setServiceFee(price.getServiceFee());
            booking.setTotalAmount(price.getTotalAmount());
            booking.setPrepaymentAmount(price.getPrepaymentAmount());
        }

        // Записать комментарий менеджера в историю (если есть)
        if (request.getManagerComment() != null && !request.getManagerComment().isBlank()) {
            bookingHistoryService.logFieldChangeWithComment(booking, "COMMENT", null,
                    null, null, request.getManagerComment().trim(), "manager");
        }

        booking = bookingRepository.save(booking);
        log.info("Booking {} updated. datesChanged={}", bookingId, datesChanged);

        return bookingConverter.convertFromEntity(booking);
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

    @Transactional(readOnly = true)
    @Override
    public List<BookingTableCalendarRow> getTableCalendarData(LocalDate from, LocalDate to) {
        List<Booking> bookings = bookingRepository.findForCalendar(from, to);

        // Group by vehicle
        Map<Long, BookingTableCalendarRow> rowMap = new LinkedHashMap<>();

        for (Booking booking : bookings) {
            Vehicle v = booking.getVehicle();
            BookingTableCalendarRow row = rowMap.computeIfAbsent(v.getId(), id -> BookingTableCalendarRow.builder()
                    .id(v.getId())
                    .brand(v.getBrand())
                    .model(v.getModel())
                    .licensePlate(v.getLicensePlate())
                    .bookings(new ArrayList<>())
                    .build());

            row.getBookings().add(BookingBarDto.builder()
                    .id(booking.getId())
                    .customerName(booking.getCustomer().getFullName())
                    .customerEmail(booking.getCustomer().getEmail())
                    .pickupDate(booking.getPickupDate().toString())
                    .dropoffDate(booking.getDropoffDate().toString())
                    .totalAmount(booking.getTotalAmount())
                    .addOns(serviceOptionService.getAddOnsNamesByBooking(booking))
                    .status(booking.getStatus().name())
                    .paymentStatus(booking.getPaymentStatus().name())
                    .build());
        }
        // Also include vehicles that have NO bookings in range (so they show in calendar)
        vehicleRepository.findAll(Sort.by("brand", "model")).forEach(v -> {
            rowMap.computeIfAbsent(v.getId(), id -> BookingTableCalendarRow.builder()
                    .id(v.getId())
                    .brand(v.getBrand())
                    .model(v.getModel())
                    .licensePlate(v.getLicensePlate())
                    .bookings(new ArrayList<>())
                    .build());
        });

        return new ArrayList<>(rowMap.values());
    }
}
