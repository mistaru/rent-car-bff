package kg.founders.core.services.rental;

import kg.founders.core.model.rental.BookingCalendarItem;
import kg.founders.core.model.rental.BookingDto;
import kg.founders.core.model.rental.CreateBookingRequest;
import kg.founders.core.model.rental.UpdateBookingRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookingService {
    @Transactional
    BookingDto createBooking(CreateBookingRequest request);

    @Transactional(readOnly = true)
    List<BookingDto> getAllBookings();

    @Transactional(readOnly = true)
    List<BookingCalendarItem> getBookingsForCalendar();

    @Transactional(readOnly = true)
    BookingDto getBookingById(Long id);

    @Transactional(readOnly = true)
    List<BookingDto> getBookingsByCustomerId(Long customerId);

    @Transactional
    BookingDto cancelBooking(Long bookingId);

    @Transactional
    BookingDto updateBooking(Long bookingId, UpdateBookingRequest request);
}
