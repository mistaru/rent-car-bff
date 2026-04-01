package kg.founders.core.model.rental;

import kg.founders.core.enums.PaymentMethod;
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

    private String success;

    private PaymentMethod paymentMethod;

    @Override
    public String toString() {
        return  bookingId + "|" + transactionId + "|" + success;
    }
}
