package kg.founders.core.converter.rental;

import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.PriceTier;
import kg.founders.core.entity.rental.PricingTemplate;
import kg.founders.core.model.rental.PricingTemplateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PricingTemplateConverter extends ModelConverter<PricingTemplateDto, PricingTemplate> {
    private final PriceTierConverter priceTierConverter;

    @PostConstruct
    public void init() {
        this.fromEntity = this::toPricingTemplateDto;
    }

    private PricingTemplateDto toPricingTemplateDto(PricingTemplate template) {
        if (template == null) return null;
        return PricingTemplateDto.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .currency(template.getCurrency())
                .active(template.getActive())
                .minPricePerDay(template.getTiers() != null
                        ? template.getTiers().stream()
                        .map(PriceTier::getPricePerDay)
                        .min(java.math.BigDecimal::compareTo)
                        .orElse(null)
                        : null)
                .tiers(template.getTiers() != null
                        ? template.getTiers().stream()
                        .map(priceTierConverter::convertFromEntity)
                        .collect(Collectors.toList())
                        : null)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
