package kg.founders.core.model.rental;

import lombok.*;

import java.math.BigDecimal;

/**
 * Облегчённый DTO для отображения бронирования в календаре.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCalendarItem {
    private Long id;
    private String vehicleName;
    private String vehicleImage;
    private String carClass;
    private String customerName;
    private String pickupDate;
    private String dropoffDate;
    private int days;
    private String status;
    private String paymentStatus;
    private BigDecimal totalAmount;
    /** CSS-цвет для отображения в календаре */
    private String color;
}

