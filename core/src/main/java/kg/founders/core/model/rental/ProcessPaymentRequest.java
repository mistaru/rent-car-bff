package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    private String transactionId;

    private boolean isSuccessful;

    @Override
    public String toString() {
        return  bookingId + "|" + transactionId + "|" + isSuccessful;
    }
}
