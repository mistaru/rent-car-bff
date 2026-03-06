package kg.founders.core.entity.rental;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Справочник характеристик автомобилей.
 * Админ может добавлять, редактировать, удалять характеристики.
 * Тип значения: ENUM (выбор из списка), TEXT, NUMBER, BOOLEAN.
 * Признак filterable — если true, фронт строит фильтр.
 * UI-логика фильтров:
 *   1 значение → не отображается,
 *   2 значения → radio,
 *   >2 значений → select.
 */
@Entity
@Table(name = "vehicle_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальный код, напр. BODY_TYPE, ENGINE_VOLUME, HAS_AC */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** Отображаемое название: «Тип кузова», «Объём двигателя» и т.д. */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Тип значения:
     * ENUM — выбор из possibleValues (через запятую)
     * TEXT — произвольный текст
     * NUMBER — число
     * BOOLEAN — да/нет
     */
    @Column(nullable = false, length = 20)
    private String valueType;

    /**
     * Допустимые значения для ENUM — через запятую.
     * Пример: "Sedan,SUV,Hatchback,Coupe,Wagon"
     * Для TEXT/NUMBER/BOOLEAN — null.
     */
    @Column(length = 1000)
    private String possibleValues;

    /** Использовать ли для фильтрации в каталоге */
    @Column(nullable = false)
    @Builder.Default
    private Boolean filterable = true;

    /** Порядок отображения */
    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    /** Активна ли характеристика */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

