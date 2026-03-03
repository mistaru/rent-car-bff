package kg.founders.core.converter;

import kg.founders.core.entity.*;
import kg.founders.core.model.rental.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RentalMapper {

    public VehicleDto toVehicleDto(Vehicle vehicle) {
        if (vehicle == null) return null;
        return VehicleDto.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .bodyType(vehicle.getBodyType())
                .drivetrain(vehicle.getDrivetrain())
                .fuelType(vehicle.getFuelType())
                .transmission(vehicle.getTransmission())
                .pricePerDay(vehicle.getPricePerDay())
                .image(vehicle.getImage())
                .status(vehicle.getStatus().name().toLowerCase())
                .carClass(vehicle.getCarClass())
                .build();
    }

    public LocationDto toLocationDto(Location location) {
        if (location == null) return null;
        return LocationDto.builder()
                .id(location.getId())
                .name(location.getName())
                .city(location.getCity())
                .country(location.getCountry())
                .build();
    }

    public CustomerDto toCustomerDto(Customer customer) {
        if (customer == null) return null;
        return CustomerDto.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
    }

    public BookingDto toBookingDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.builder()
                .id(booking.getId())
                .vehicle(toVehicleDto(booking.getVehicle()))
                .customer(toCustomerDto(booking.getCustomer()))
                .pickupLocation(toLocationDto(booking.getPickupLocation()))
                .dropoffLocation(toLocationDto(booking.getDropoffLocation()))
                .pickupDate(booking.getPickupDate())
                .dropoffDate(booking.getDropoffDate())
                .days(booking.getDays())
                .baseAmount(booking.getBaseAmount())
                .addOnsAmount(booking.getAddOnsAmount())
                .serviceFee(booking.getServiceFee())
                .totalAmount(booking.getTotalAmount())
                .currency(booking.getCurrency())
                .status(booking.getStatus().name())
                .paymentStatus(booking.getPaymentStatus().name())
                .addOns(booking.getAddOns() != null
                        ? booking.getAddOns().stream()
                            .map(a -> a.getAddOnType().name())
                            .collect(Collectors.toList())
                        : null)
                .createdAt(booking.getCreatedAt())
                .build();
    }

    public PaymentDto toPaymentDto(Payment payment) {
        if (payment == null) return null;
        return PaymentDto.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .method(payment.getMethod().name())
                .status(payment.getStatus().name())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Customer toCustomerEntity(CreateCustomerRequest request) {
        return Customer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
    }
}
