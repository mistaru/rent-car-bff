package kg.founders.core.services.rental;

import kg.founders.core.model.rental.PaymentDto;
import kg.founders.core.model.rental.ProcessPaymentRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PaymentService {
    @Transactional(readOnly = true)
    List<PaymentDto> getAllPayments();

    @Transactional
    PaymentDto initiatePayment(Long bookingId);

    @Transactional
    PaymentDto processPayment(ProcessPaymentRequest request);
}
