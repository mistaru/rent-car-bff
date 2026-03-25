package kg.founders.core.entity.rental;

import kg.founders.core.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicle_status", columnList = "status"),
        @Index(name = "idx_vehicle_location", columnList = "location_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;


    @Column(nullable = false)
    private String licensePlate;

    @Column(length = 1024)
    private String image;

    private String carClass;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    /** Кэшированная минимальная цена из тарифного шаблона (для каталога — «от X $») */
    @Column(precision = 10, scale = 2)
    private BigDecimal minPricePerDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_template_id")
    private PricingTemplate pricingTemplate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Version
    private Long version;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<VehicleImage> images = new ArrayList<>();
}
