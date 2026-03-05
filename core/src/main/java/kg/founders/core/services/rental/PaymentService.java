package kg.founders.core.services.rental;

import kg.founders.core.converter.RentalMapper;
import kg.founders.core.entity.rental.Booking;
import kg.founders.core.entity.rental.Payment;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.enums.*;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.exceptions.PaymentFailedException;
import kg.founders.core.model.rental.PaymentDto;
import kg.founders.core.model.rental.ProcessPaymentRequest;
import kg.founders.core.repo.BookingRepository;
import kg.founders.core.repo.PaymentRepository;
import kg.founders.core.repo.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {


    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalMapper rentalMapper;

    @Transactional
    public PaymentDto initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new PaymentFailedException("Booking is not in PENDING_PAYMENT status. Current status: " + booking.getStatus());
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .method(PaymentMethod.ONLINE)
                .status(PaymentTransactionStatus.INITIATED)
                .amount(booking.getTotalAmount())
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment initiated for booking {}, payment id: {}", bookingId, payment.getId());

        return rentalMapper.toPaymentDto(payment);
    }

    @Transactional
    public PaymentDto processPayment(ProcessPaymentRequest request) {
        Booking booking = bookingRepository.findByIdWithDetails(request.getBookingId())
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + request.getBookingId()));

        Payment payment = paymentRepository.findByBookingId(request.getBookingId()).stream()
                .filter(p -> p.getStatus() == PaymentTransactionStatus.INITIATED)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No initiated payment found for booking: " + request.getBookingId()));

        if (request.isSuccess()) {
            return handleSuccessfulPayment(booking, payment, request.getTransactionId());
        } else {
            return handleFailedPayment(booking, payment);
        }
    }

    private PaymentDto handleSuccessfulPayment(Booking booking, Payment payment, String transactionId) {
        // Update payment
        payment.setStatus(PaymentTransactionStatus.SUCCESS);
        payment.setTransactionId(transactionId);
        paymentRepository.save(payment);

        // Update booking
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.PAID);
        bookingRepository.save(booking);

        // Update vehicle
        Vehicle vehicle = booking.getVehicle();
        vehicle.setStatus(VehicleStatus.BOOKED);
        vehicleRepository.save(vehicle);

        log.info("Payment successful for booking {}, transaction: {}", booking.getId(), transactionId);

        return rentalMapper.toPaymentDto(payment);
    }

    private PaymentDto handleFailedPayment(Booking booking, Payment payment) {
        // Update payment
        payment.setStatus(PaymentTransactionStatus.FAILED);
        paymentRepository.save(payment);

        // Update booking
        booking.setPaymentStatus(PaymentStatus.FAILED);
        bookingRepository.save(booking);

        // Release vehicle
        Vehicle vehicle = booking.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        log.warn("Payment failed for booking {}", booking.getId());

        return rentalMapper.toPaymentDto(payment);
    }
}
