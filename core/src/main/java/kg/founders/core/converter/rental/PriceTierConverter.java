package kg.founders.core.converter.rental;

import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.PriceTier;
import kg.founders.core.model.rental.PriceTierDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class PriceTierConverter extends ModelConverter<PriceTierDto, PriceTier> {
    @PostConstruct
    public void init() {
        this.fromEntity = this::toPriceTierDto;
    }

    private PriceTierDto toPriceTierDto(PriceTier tier) {
        if (tier == null) return null;
        return PriceTierDto.builder()
                .id(tier.getId())
                .minDays(tier.getMinDays())
                .maxDays(tier.getMaxDays())
                .pricePerDay(tier.getPricePerDay())
                .build();
    }
}
