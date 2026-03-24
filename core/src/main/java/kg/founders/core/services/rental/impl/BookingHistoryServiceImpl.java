package kg.founders.core.services.rental.impl;

import kg.founders.core.entity.rental.Booking;
import kg.founders.core.entity.rental.BookingHistory;
import kg.founders.core.model.rental.BookingHistoryDto;
import kg.founders.core.repo.BookingHistoryRepository;
import kg.founders.core.repo.BookingRepository;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.services.rental.BookingHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingHistoryServiceImpl implements BookingHistoryService {

    private final BookingHistoryRepository historyRepository;
    private final BookingRepository bookingRepository;

    /**
     * Получить историю изменений бронирования.
     */
    @Transactional(readOnly = true)
    @Override
    public List<BookingHistoryDto> getHistoryByBookingId(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Booking not found with id: " + bookingId);
        }
        return historyRepository.findByBookingIdOrderByCreatedAtDesc(bookingId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Записать событие «Бронирование создано».
     */
    @Transactional
    @Override
    public void logCreated(Booking booking, String performedBy) {
        save(booking, "CREATED", null, null,
                "Бронирование создано. Авто: " + booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel()
                        + ", Клиент: " + booking.getCustomer().getFullName(),
                null, performedBy);
    }

    /**
     * Записать событие «Бронирование отменено».
     */
    @Transactional
    @Override
    public void logCancelled(Booking booking, String performedBy) {
        save(booking, "CANCELLED", "status",
                booking.getStatus().name(), "CANCELLED", null, performedBy);
    }

    /**
     * Записать изменение конкретного поля.
     */
    @Transactional
    @Override
    public void logFieldChange(Booking booking, String action, String field,
                               String oldValue, String newValue, String performedBy) {
        save(booking, action, field, oldValue, newValue, null, performedBy);
    }

    /**
     * Записать изменение с комментарием.
     */
    @Transactional
    @Override
    public void logFieldChangeWithComment(Booking booking, String action, String field,
                                          String oldValue, String newValue,
                                          String comment, String performedBy) {
        save(booking, action, field, oldValue, newValue, comment, performedBy);
    }

    private void save(Booking booking, String action, String field,
                       String oldValue, String newValue,
                       String comment, String performedBy) {
        BookingHistory history = BookingHistory.builder()
                .booking(booking)
                .action(action)
                .field(field)
                .oldValue(oldValue)
                .newValue(newValue)
                .comment(comment)
                .performedBy(performedBy != null ? performedBy : "system")
                .build();
        historyRepository.save(history);
        log.debug("BookingHistory: booking={}, action={}, field={}", booking.getId(), action, field);
    }

    private BookingHistoryDto toDto(BookingHistory h) {
        return BookingHistoryDto.builder()
                .id(h.getId())
                .bookingId(h.getBooking().getId())
                .action(h.getAction())
                .field(h.getField())
                .oldValue(h.getOldValue())
                .newValue(h.getNewValue())
                .comment(h.getComment())
                .performedBy(h.getPerformedBy())
                .createdAt(h.getCreatedAt())
                .build();
    }
}

