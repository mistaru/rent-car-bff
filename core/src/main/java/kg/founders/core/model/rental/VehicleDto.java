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
    private String bodyType;
    private String drivetrain;
    private String fuelType;
    private String transmission;
    private BigDecimal pricePerDay;
    /** Минимальная цена из тарифного шаблона (для каталога — «от X $») */
    private BigDecimal minPricePerDay;
    private String image;
    private String status;
    private String carClass;
    private String pricingTemplateName;
    private Long pricingTemplateId;
    private Long locationId;

    private List<VehicleImageDto> photos;

    /** Dynamic vehicle attributes: code -> value, e.g. {"SEATS": "5", "COLOR": "Black"} */
    private Map<String, String> attributes;
}
