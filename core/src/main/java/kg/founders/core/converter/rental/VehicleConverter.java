package kg.founders.core.converter.rental;

import kg.founders.core.config.ImageStorageProperties;
import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.model.rental.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VehicleConverter extends ModelConverter<VehicleDto, Vehicle> {

    private final ImageStorageProperties imageProps;

    @PostConstruct
    public void init() {
        this.fromEntity = this::toVehicleDto;
    }

    private VehicleDto toVehicleDto(Vehicle vehicle) {
        if (vehicle == null) return null;

        // Собираем URL-ы изображений (main первым)
        List<String> imageUrls = List.of();
        if (vehicle.getImages() != null && !vehicle.getImages().isEmpty()) {
            imageUrls = vehicle.getImages().stream()
                    .sorted((a, b) -> {
                        if (a.isMain() && !b.isMain()) return -1;
                        if (!a.isMain() && b.isMain()) return 1;
                        return Integer.compare(
                                a.getSortOrder() != null ? a.getSortOrder() : 0,
                                b.getSortOrder() != null ? b.getSortOrder() : 0);
                    })
                    .filter(img -> img.getStorageFilename() != null)
                    .map(img -> imageProps.getBaseUrl() + "/" + img.getStorageFilename())
                    .collect(Collectors.toList());
        }

        return VehicleDto.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .licensePlate(vehicle.getLicensePlate())
                .pricePerDay(vehicle.getPricePerDay())
                .minPricePerDay(vehicle.getMinPricePerDay())
                .status(vehicle.getStatus().name().toLowerCase())
                .carClass(vehicle.getCarClass())
                .locationId(vehicle.getLocation() != null ? vehicle.getLocation().getId() : null)
                .pricingTemplateName(vehicle.getPricingTemplate() != null
                        ? vehicle.getPricingTemplate().getName() : null)
                .pricingTemplateId(vehicle.getPricingTemplate() != null
                        ? vehicle.getPricingTemplate().getId() : null)
                .images(imageUrls)
                .build();
    }
}
