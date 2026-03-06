package kg.founders.core.model.rental;

import lombok.*;

import java.util.List;

/**
 * DTO для характеристики автомобиля (справочник).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAttributeDto {
    private Long id;
    private String code;
    private String name;
    /** ENUM, TEXT, NUMBER, BOOLEAN */
    private String valueType;
    /** Для ENUM: список допустимых значений */
    private List<String> possibleValues;
    private Boolean filterable;
    private Integer sortOrder;
    private Boolean active;

    /**
     * Для фильтров в каталоге: реальные значения, используемые в авто.
     * Заполняется только при запросе фильтров.
     */
    private List<String> usedValues;

    /**
     * Рекомендуемый тип UI-фильтра (вычисляется автоматически):
     * "none" — 0–1 значение, не показывать;
     * "radio" — 2 значения;
     * "select" — >2 значений;
     * "checkbox" — BOOLEAN
     */
    private String filterType;
}

