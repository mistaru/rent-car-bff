package kg.founders.core.model.rental;

import kg.founders.core.enums.AddOnType;
import kg.founders.core.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Pickup location ID is required")
    private Long pickupLocationId;

    @NotNull(message = "Dropoff location ID is required")
    private Long dropoffLocationId;

    @NotNull(message = "Pickup date is required")
    @Future(message = "Pickup date must be in the future")
    private LocalDate pickupDate;

    @NotNull(message = "Dropoff date is required")
    @Future(message = "Dropoff date must be in the future")
    private LocalDate dropoffDate;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private List<AddOnType> addOns;

    private String currency;
}
