package kg.founders.core.model.rental;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingTableCalendarRow {
    private Long   id;
    private String brand;
    private String model;
    private String licensePlate;
    private Integer year;
    private List<BookingBarDto> bookings;
}
