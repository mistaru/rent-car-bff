package kg.founders.core.repo;

import kg.founders.core.entity.rental.BookingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingDocumentRepository extends JpaRepository<BookingDocument, Long> {
    List<BookingDocument> findByBookingId(Long bookingId);
    void deleteByBookingIdAndId(Long bookingId, Long id);
}
