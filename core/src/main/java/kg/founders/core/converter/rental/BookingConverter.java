package kg.founders.core.converter.rental;

import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.Booking;
import kg.founders.core.model.rental.BookingDto;
import kg.founders.core.model.rental.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingConverter extends ModelConverter<BookingDto, Booking> {
    private final VehicleConverter vehicleConverter;
    private final CustomerConverter customerConverter;
    private final LocationConverter locationConverter;

    @PostConstruct
    public void init() {
        this.fromEntity = this::convertToDto;
    }

    private BookingDto convertToDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.builder()
                .id(booking.getId())
                .vehicle(vehicleConverter.convertFromEntity(booking.getVehicle()))
                .customer((CustomerDto) customerConverter.convertFromEntity(booking.getCustomer()))
                .pickupLocation(locationConverter.convertFromEntity(booking.getPickupLocation()))
                .dropoffLocation(locationConverter.convertFromEntity(booking.getDropoffLocation()))
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
                        .map(a -> BookingDto.BookingAddOnDto.builder()
                                .code(a.getAddOnType().name())
                                .name(a.getAddOnType().name().replace('_', ' '))
                                .pricePerDay(a.getPricePerDay())
                                .quantity(a.getQuantity())
                                .build())
                        .collect(Collectors.toList())
                        : null)
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
