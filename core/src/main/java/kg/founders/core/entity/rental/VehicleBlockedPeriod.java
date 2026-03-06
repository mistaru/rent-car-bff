package kg.founders.core.entity.rental;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Блокировка автомобиля на определённый период (техобслуживание, ремонт и т.д.).
 * Проверяется в AvailabilityService при бронировании.
 */
@Entity
@Table(name = "vehicle_blocked_periods", indexes = {
        @Index(name = "idx_blocked_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_blocked_dates", columnList = "start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleBlockedPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /** Причина блокировки (техобслуживание, ремонт, сезонное хранение и т.д.) */
    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

