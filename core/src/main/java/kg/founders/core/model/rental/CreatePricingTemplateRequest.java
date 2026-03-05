package kg.founders.core.model.rental;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePricingTemplateRequest {

    @NotBlank(message = "Template name is required")
    private String name;

    private String description;

    private String currency;

    @NotEmpty(message = "At least one price tier is required")
    @Valid
    private List<PriceTierDto> tiers;
}

