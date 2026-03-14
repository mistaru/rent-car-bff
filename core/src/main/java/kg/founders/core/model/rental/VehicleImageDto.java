package kg.founders.core.model.rental;

import lombok.Builder;
import lombok.Data;

// Response — без byte[], только мета + url для отображения
@Data
@Builder
public class VehicleImageDto {
    private Long id;
    private Long vehicleId;
    private String filename;
    private String mimeType;
    private boolean main;
    private int sortOrder;
    private String url; // /api/v1/vehicle-images/{id}/data
}