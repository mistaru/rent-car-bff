package kg.founders.core.entity.rental;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Дополнительная услуга (инвентарь, доставка, документы и т.д.).
 * Управляется из админки — CRUD вместо хардкода в enum.
 */
@Entity
@Table(name = "service_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальный код услуги, например ROOF_TENT. Используется для привязки к бронированиям. */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** Отображаемое название */
    @Column(nullable = false)
    private String name;

    /** Описание услуги */
    @Column(length = 500)
    private String description;

    /** Категория: EQUIPMENT, DELIVERY, DOCUMENTS, OTHER */
    @Column(nullable = false, length = 30)
    private String category;

    /** MDI-иконка для отображения на фронте */
    @Column(length = 50)
    private String icon;

    /** Цена за день аренды */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    /** Активна ли услуга (можно временно отключить) */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /** Порядок сортировки */
    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    /** Общее количество единиц (null = неограничено, напр. для документов/доставки) */
    private Integer totalQuantity;

    /** Доступное количество (рассчитывается: total - занятые в активных бронях) */
    @Transient
    private Integer availableQuantity;

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

