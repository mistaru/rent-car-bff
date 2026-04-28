package kg.founders.bff.controller.rental;

import kg.founders.core.model.rental.*;
import kg.founders.core.services.rental.BookingService;
import kg.founders.core.services.rental.impl.BookingHistoryServiceImpl;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingHistoryServiceImpl bookingHistoryService;

    @ManualPermissionControl
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingDto booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @ManualPermissionControl
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @ManualPermissionControl
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingDto>> getBookingsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getBookingsByCustomerId(customerId));
    }

    @ManualPermissionControl
    @PutMapping("/{id}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable Long id,
                                                    @RequestBody UpdateBookingRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @ManualPermissionControl
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @ManualPermissionControl
    @GetMapping("/{id}/history")
    public ResponseEntity<List<BookingHistoryDto>> getBookingHistory(@PathVariable Long id) {
        return ResponseEntity.ok(bookingHistoryService.getHistoryByBookingId(id));
    }

    @ManualPermissionControl
    @GetMapping("/calendar")
    public ResponseEntity<List<BookingCalendarItem>> getBookingsCalendar() {
        return ResponseEntity.ok(bookingService.getBookingsForCalendar());
    }

    @ManualPermissionControl
    @GetMapping("/table-calendar")
    public ResponseEntity<List<BookingTableCalendarRow>> getTableCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(bookingService.getTableCalendarData(from, to));
    }
}
