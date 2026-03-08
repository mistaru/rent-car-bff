package kg.founders.core.model.rental;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehiclePhotosDto {
    private Long id;
    private String url;
    private Integer sortOrder;
}