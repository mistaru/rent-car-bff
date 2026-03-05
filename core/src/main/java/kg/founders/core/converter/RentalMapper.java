package kg.founders.core.converter;

import kg.founders.core.entity.rental.*;
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
                .minPricePerDay(vehicle.getMinPricePerDay())
                .image(vehicle.getImage())
                .status(vehicle.getStatus().name().toLowerCase())
                .carClass(vehicle.getCarClass())
                .pricingTemplateName(vehicle.getPricingTemplate() != null
                        ? vehicle.getPricingTemplate().getName() : null)
                .pricingTemplateId(vehicle.getPricingTemplate() != null
                        ? vehicle.getPricingTemplate().getId() : null)
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
                .pricePerDay(booking.getPricePerDay())
                .priceTierDescription(booking.getPriceTierDescription())
                .baseAmount(booking.getBaseAmount())
                .addOnsAmount(booking.getAddOnsAmount())
                .serviceFee(booking.getServiceFee())
                .totalAmount(booking.getTotalAmount())
                .prepaymentAmount(booking.getPrepaymentAmount())
                .prepaymentPaid(booking.getPrepaymentPaid())
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

    public PricingTemplateDto toPricingTemplateDto(PricingTemplate template) {
        if (template == null) return null;
        return PricingTemplateDto.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .currency(template.getCurrency())
                .active(template.getActive())
                .minPricePerDay(template.getTiers() != null
                        ? template.getTiers().stream()
                            .map(PriceTier::getPricePerDay)
                            .min(java.math.BigDecimal::compareTo)
                            .orElse(null)
                        : null)
                .tiers(template.getTiers() != null
                        ? template.getTiers().stream()
                            .map(this::toPriceTierDto)
                            .collect(Collectors.toList())
                        : null)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }

    public PriceTierDto toPriceTierDto(PriceTier tier) {
        if (tier == null) return null;
        return PriceTierDto.builder()
                .id(tier.getId())
                .minDays(tier.getMinDays())
                .maxDays(tier.getMaxDays())
                .pricePerDay(tier.getPricePerDay())
                .build();
    }
}
