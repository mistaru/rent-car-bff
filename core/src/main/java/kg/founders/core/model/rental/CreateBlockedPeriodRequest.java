package kg.founders.core.model.rental;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBlockedPeriodRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Reason is required")
    private String reason;
}

