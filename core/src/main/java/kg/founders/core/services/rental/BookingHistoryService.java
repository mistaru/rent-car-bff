package kg.founders.core.services.rental;

import kg.founders.core.entity.rental.Booking;
import kg.founders.core.model.rental.BookingHistoryDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookingHistoryService {
    @Transactional(readOnly = true)
    List<BookingHistoryDto> getHistoryByBookingId(Long bookingId);

    @Transactional
    void logCreated(Booking booking, String performedBy);

    @Transactional
    void logCancelled(Booking booking, String performedBy);

    @Transactional
    void logFieldChange(Booking booking, String action, String field,
                        String oldValue, String newValue, String performedBy);

    @Transactional
    void logFieldChangeWithComment(Booking booking, String action, String field,
                                   String oldValue, String newValue,
                                   String comment, String performedBy);
}
