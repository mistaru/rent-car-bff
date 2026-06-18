package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private BigDecimal pricePerDay;
    /** Минимальная цена из тарифного шаблона (для каталога — «от X $») */
    private BigDecimal minPricePerDay;
    private String status;
    private String carClass;
    private String pricingTemplateName;
    private Long pricingTemplateId;
    private Long locationId;

    /** URL-ы изображений (для фронтенда) */
    private List<String> images;

    /** Dynamic vehicle attributes: code -> value, e.g. {"SEATS": "5", "COLOR": "Black"} */
    private Map<String, String> attributes;
}
