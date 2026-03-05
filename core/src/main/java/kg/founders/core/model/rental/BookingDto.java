package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BookingDto {
    private final Long id;
    private final VehicleDto vehicle;
    private final CustomerDto customer;
    private final LocationDto pickupLocation;
    private final LocationDto dropoffLocation;
    private final LocalDate pickupDate;
    private final LocalDate dropoffDate;
    private final Integer days;
    private final BigDecimal pricePerDay;
    private final String priceTierDescription;
    private final BigDecimal baseAmount;
    private final BigDecimal addOnsAmount;
    private final BigDecimal serviceFee;
    private final BigDecimal totalAmount;
    private final BigDecimal prepaymentAmount;
    private final Boolean prepaymentPaid;
    private final String currency;
    private final String status;
    private final String paymentStatus;
    private final List<String> addOns;
    private final LocalDateTime createdAt;
}
