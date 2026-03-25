package kg.founders.core.converter.rental;

import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.model.rental.VehicleDto;
import kg.founders.core.model.rental.VehicleImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VehicleConverter extends ModelConverter<VehicleDto, Vehicle> {
    @PostConstruct
    public void init() {
        this.fromEntity = this::toVehicleDto;
    }

    private VehicleDto toVehicleDto(Vehicle vehicle) {
        if (vehicle == null) return null;
        VehicleDto dto = VehicleDto.builder()
                .id(vehicle.getId())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .licensePlate(vehicle.getLicensePlate())
                .pricePerDay(vehicle.getPricePerDay())
                .minPricePerDay(vehicle.getMinPricePerDay())
                .image(vehicle.getImage())
                .status(vehicle.getStatus().name().toLowerCase())
                .carClass(vehicle.getCarClass())
                .locationId(vehicle.getLocation() != null ? vehicle.getLocation().getId() : null)
                .pricingTemplateName(vehicle.getPricingTemplate() != null
                        ? vehicle.getPricingTemplate().getName() : null)
                .pricingTemplateId(vehicle.getPricingTemplate() != null
                        ? vehicle.getPricingTemplate().getId() : null)
                .build();

        if (vehicle.getImages() != null) {
            dto.setImages(vehicle.getImages().stream()
                    .map(img -> VehicleImageDto.builder()
                            .id(img.getId())
                            .url("/api/v1/vehicle-images/" + img.getId() + "/data")
                            .main(img.isMain())
                            .sortOrder(img.getSortOrder())
                            .build())
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
