package kg.founders.bff.controller.rental;

import kg.founders.core.enums.AuditAction;
import kg.founders.core.model.rental.InitiatePaymentRequest;
import kg.founders.core.model.rental.PaymentDto;
import kg.founders.core.model.rental.ProcessPaymentRequest;
import kg.founders.core.services.rental.PaymentService;
import kg.founders.core.settings.audit.Auditable;
import kg.founders.core.settings.audit.AuditEntityId;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    @ManualPermissionControl
    @Auditable(entity = "PAYMENT", action = AuditAction.CREATE)
    @PostMapping("/initiate/{bookingId}")
    public ResponseEntity<PaymentDto> initiatePayment(@AuditEntityId @PathVariable Long bookingId,
                                                      @Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiatePayment(bookingId, request.getAmount()));
    }

    @ManualPermissionControl
    @Auditable(entity = "PAYMENT", action = AuditAction.STATUS_CHANGE)
    @PostMapping("/process")
    public ResponseEntity<PaymentDto> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }
}
