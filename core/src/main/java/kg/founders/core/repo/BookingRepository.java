package kg.founders.core.repo;

import kg.founders.core.entity.rental.Booking;
import kg.founders.core.enums.AddOnType;
import kg.founders.core.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.vehicle " +
            "JOIN FETCH b.customer " +
            "JOIN FETCH b.pickupLocation " +
            "JOIN FETCH b.dropoffLocation " +
            "WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.vehicle.id = :vehicleId " +
            "AND b.status NOT IN (:excludedStatuses) " +
            "AND b.pickupDate < :dropoffDate " +
            "AND b.dropoffDate > :pickupDate")
    boolean existsOverlappingBooking(
            @Param("vehicleId") Long vehicleId,
            @Param("pickupDate") LocalDate pickupDate,
            @Param("dropoffDate") LocalDate dropoffDate,
            @Param("excludedStatuses") List<BookingStatus> excludedStatuses);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.vehicle " +
            "JOIN FETCH b.customer " +
            "WHERE b.customer.id = :customerId " +
            "ORDER BY b.createdAt DESC")
    List<Booking> findByCustomerIdWithDetails(@Param("customerId") Long customerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.vehicle " +
            "JOIN FETCH b.customer " +
            "JOIN FETCH b.pickupLocation " +
            "JOIN FETCH b.dropoffLocation " +
            "ORDER BY b.createdAt DESC")
    List<Booking> findAllWithDetails();

    /** Count how many active bookings currently use a given add-on (for inventory) */
    @Query("SELECT COUNT(a) FROM BookingAddOn a " +
            "WHERE a.addOnType = :addOnType " +
            "AND a.booking.status NOT IN (kg.founders.core.enums.BookingStatus.CANCELLED) " +
            "AND a.booking.dropoffDate >= :today")
    long countActiveAddOnUsage(@Param("addOnType") AddOnType addOnType,
                                @Param("today") LocalDate today);
}
