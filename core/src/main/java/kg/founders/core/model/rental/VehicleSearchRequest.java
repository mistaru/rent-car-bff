package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSearchRequest {
    private Long locationId;
    private String brand;
    private String carClass;
    private String drivetrain;
    private String fuelType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    @Future(message = "Pickup date must be in the future")
    private LocalDate pickupDate;

    @Future(message = "Dropoff date must be in the future")
    private LocalDate dropoffDate;

    /** Dynamic attribute filters: code -> value, e.g. {"BODY_TYPE": "SUV", "SEATS": "5"} */
    private Map<String, String> attributeFilters;

    private int page = 0;
    private int size = 20;
}
