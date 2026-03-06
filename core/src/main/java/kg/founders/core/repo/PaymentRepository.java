package kg.founders.core.repo;

import kg.founders.core.entity.rental.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionId(String transactionId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.booking b JOIN FETCH b.vehicle ORDER BY p.createdAt DESC")
    List<Payment> findAllWithBookingDetails();
}
