package kg.founders.core.model.rental;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingTemplateDto {
    private Long id;
    private String name;
    private String description;
    private String currency;
    private Boolean active;
    private BigDecimal minPricePerDay;
    private List<PriceTierDto> tiers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

