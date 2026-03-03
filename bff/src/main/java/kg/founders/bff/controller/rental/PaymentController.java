package kg.founders.bff.controller.rental;

import kg.founders.core.model.rental.PaymentDto;
import kg.founders.core.model.rental.ProcessPaymentRequest;
import kg.founders.core.services.rental.PaymentService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @ManualPermissionControl
    @PostMapping("/initiate/{bookingId}")
    public ResponseEntity<PaymentDto> initiatePayment(@PathVariable Long bookingId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiatePayment(bookingId));
    }

    @ManualPermissionControl
    @PostMapping("/process")
    public ResponseEntity<PaymentDto> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }
}
