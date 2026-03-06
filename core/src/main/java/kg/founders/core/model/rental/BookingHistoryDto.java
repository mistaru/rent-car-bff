package kg.founders.core.model.rental;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHistoryDto {
    private Long id;
    private Long bookingId;
    private String action;
    private String field;
    private String oldValue;
    private String newValue;
    private String comment;
    private String performedBy;
    private LocalDateTime createdAt;
}

