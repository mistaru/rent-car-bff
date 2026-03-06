package kg.founders.core.entity.rental;

import lombok.*;

import javax.persistence.*;

/**
 * Значение характеристики для конкретного автомобиля.
 * Например: vehicle_id=1, attribute="BODY_TYPE", value="SUV".
 */
@Entity
@Table(name = "vehicle_attribute_values",
        uniqueConstraints = @UniqueConstraint(columnNames = {"vehicle_id", "attribute_id"}),
        indexes = {
                @Index(name = "idx_vav_vehicle", columnList = "vehicle_id"),
                @Index(name = "idx_vav_attribute", columnList = "attribute_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private VehicleAttribute attribute;

    /** Значение (строковое представление, для любого типа) */
    @Column(nullable = false, length = 500)
    private String value;
}

