package kg.founders.core.model.rental;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOptionDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String category;
    private String icon;
    private BigDecimal pricePerDay;
    private Boolean active;
    private Integer sortOrder;

    /** Общее количество единиц (null = неограничено) */
    private Integer totalQuantity;
    /** Доступное количество на текущий момент */
    private Integer availableQuantity;
    /** Есть ли ограничение по количеству */
    private Boolean hasInventoryLimit;
}

