package kg.founders.core.entity.rental;

import kg.founders.core.enums.BookingStatus;
import kg.founders.core.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_booking_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_booking_dates", columnList = "pickup_date, dropoff_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id", nullable = false)
    private Location pickupLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropoff_location_id", nullable = false)
    private Location dropoffLocation;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "dropoff_date", nullable = false)
    private LocalDate dropoffDate;

    @Column(nullable = false)
    private Integer days;

    /** Зафиксированная цена за день на момент подтверждения бронирования */
    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    /** Описание выбранного тарифного диапазона, например «Тариф 8–14 дней» */
    @Column(length = 100)
    private String priceTierDescription;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal baseAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal addOnsAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal serviceFee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /** Сумма предоплаты (15% от totalAmount) */
    @Column(precision = 12, scale = 2)
    private BigDecimal prepaymentAmount;

    /** Оплачена ли предоплата */
    @Column(nullable = false)
    @Builder.Default
    private Boolean prepaymentPaid = false;

    /** Сервисный день блокировки ДО начала аренды */
    @Column(name = "service_block_start")
    private LocalDate serviceBlockStart;

    /** Сервисный день блокировки ПОСЛЕ окончания аренды */
    @Column(name = "service_block_end")
    private LocalDate serviceBlockEnd;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookingAddOn> addOns = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
