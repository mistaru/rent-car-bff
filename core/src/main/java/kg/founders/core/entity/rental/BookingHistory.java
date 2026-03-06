package kg.founders.core.entity.rental;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Лог изменений бронирования.
 * Каждая запись — одно действие (создание, обновление, отмена, оплата).
 */
@Entity
@Table(name = "booking_history", indexes = {
        @Index(name = "idx_bh_booking", columnList = "booking_id"),
        @Index(name = "idx_bh_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * Тип действия: CREATED, UPDATED, CANCELLED, PAYMENT_INITIATED,
     * PAYMENT_COMPLETED, STATUS_CHANGED, DATES_CHANGED, CUSTOMER_UPDATED
     */
    @Column(nullable = false, length = 30)
    private String action;

    /** Поле которое изменилось (null для CREATED/CANCELLED) */
    @Column(length = 50)
    private String field;

    /** Старое значение */
    @Column(length = 500)
    private String oldValue;

    /** Новое значение */
    @Column(length = 500)
    private String newValue;

    /** Комментарий менеджера (опционально) */
    @Column(length = 500)
    private String comment;

    /** Кто внёс изменение (username / system) */
    @Column(nullable = false, length = 100)
    private String performedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

