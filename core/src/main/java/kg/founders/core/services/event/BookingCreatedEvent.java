package kg.founders.core.services.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookingCreatedEvent extends ApplicationEvent {

    private final Long bookingId;
    private final Long vehicleId;
    private final Long customerId;

    public BookingCreatedEvent(Object source, Long bookingId, Long vehicleId, Long customerId) {
        super(source);
        this.bookingId = bookingId;
        this.vehicleId = vehicleId;
        this.customerId = customerId;
    }
}
