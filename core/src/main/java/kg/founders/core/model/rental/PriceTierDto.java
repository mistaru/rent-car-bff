package kg.founders.core.model.rental;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTierDto {
    private Long id;
    private Integer minDays;
    private Integer maxDays;
    private BigDecimal pricePerDay;
}

