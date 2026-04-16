package kg.founders.core.entity.rental;

import kg.founders.core.enums.AddOnType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_add_ons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingAddOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddOnType addOnType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    /** Количество единиц данной услуги */
    @Column(nullable = false, columnDefinition = "integer default 1")
    @Builder.Default
    private Integer quantity = 1;
}
