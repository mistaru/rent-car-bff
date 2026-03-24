package kg.founders.core.converter.rental;

import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.Payment;
import kg.founders.core.model.rental.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class PaymentConverter extends ModelConverter<PaymentDto, Payment> {
    @PostConstruct
    public void init() {
        this.fromEntity = this::toPaymentDto;
    }

    private PaymentDto toPaymentDto(Payment payment) {
        if (payment == null) return null;
        return PaymentDto.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
