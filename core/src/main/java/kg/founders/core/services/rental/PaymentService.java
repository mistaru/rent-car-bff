package kg.founders.core.services.rental;

import kg.founders.core.model.rental.PaymentDto;
import kg.founders.core.model.rental.ProcessPaymentRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    @Transactional(readOnly = true)
    List<PaymentDto> getAllPayments();

    @Transactional
    PaymentDto initiatePrepayment(Long bookingId);

    @Transactional
    PaymentDto initiatePayment(Long bookingId, BigDecimal amount);

    @Transactional
    PaymentDto processPayment(ProcessPaymentRequest request);

    void deletePaymentByBookingId(Long bookingId);
}
