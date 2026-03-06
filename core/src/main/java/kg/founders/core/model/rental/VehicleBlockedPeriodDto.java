package kg.founders.core.model.rental;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleBlockedPeriodDto {
    private Long id;
    private Long vehicleId;
    private String vehicleName; // "Brand Model"
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LocalDateTime createdAt;
}

