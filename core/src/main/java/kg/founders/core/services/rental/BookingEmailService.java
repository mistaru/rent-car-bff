package kg.founders.core.services.rental;

import kg.founders.core.entity.rental.Booking;

public interface BookingEmailService {
    void sendBookingConfirmation(Booking booking);
}
