package kg.founders.core.model.rental;

import lombok.*;

/**
 * Запрос на добавление доп. услуги к бронированию с указанием количества.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddOnRequest {
    /** Код услуги (например ROOF_TENT) */
    private String code;
    /** Количество (по умолчанию 1) */
    private Integer quantity;

    public int getEffectiveQuantity() {
        return quantity != null && quantity > 0 ? quantity : 1;
    }
}

