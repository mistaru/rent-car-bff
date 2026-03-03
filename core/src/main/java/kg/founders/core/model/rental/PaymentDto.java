package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PaymentDto {
    private final Long id;
    private final Long bookingId;
    private final String method;
    private final String status;
    private final BigDecimal amount;
    private final String transactionId;
    private final LocalDateTime createdAt;
}
