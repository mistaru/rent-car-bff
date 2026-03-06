package kg.founders.core.repo;

import kg.founders.core.entity.rental.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistory, Long> {

    @Query("SELECT bh FROM BookingHistory bh WHERE bh.booking.id = :bookingId ORDER BY bh.createdAt DESC")
    List<BookingHistory> findByBookingIdOrderByCreatedAtDesc(@Param("bookingId") Long bookingId);
}
