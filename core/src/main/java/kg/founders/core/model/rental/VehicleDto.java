package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class VehicleDto {
    private final Long id;
    private final String brand;
    private final String model;
    private final Integer year;
    private final String bodyType;
    private final String drivetrain;
    private final String fuelType;
    private final String transmission;
    private final BigDecimal pricePerDay;
    /** Минимальная цена из тарифного шаблона (для каталога — «от X $») */
    private final BigDecimal minPricePerDay;
    private final String image;
    private final String status;
    private final String carClass;
    private final String pricingTemplateName;
    private final Long pricingTemplateId;
}
