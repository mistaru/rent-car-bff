package kg.founders.core.converter.rental;

import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.VehicleImage;
import kg.founders.core.model.rental.VehicleImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class VehicleImageConverter extends ModelConverter<VehicleImageDto, VehicleImage> {
    @PostConstruct
    public void init() {
        this.fromEntity = this::toVehicleImageDto;
    }

    private VehicleImageDto toVehicleImageDto(VehicleImage image) {
        if (image == null) return null;
        return VehicleImageDto.builder()
                .id(image.getId())
                .vehicleId(image.getVehicle().getId())
                .filename(image.getFilename())
                .mimeType(image.getMimeType())
                .main(image.isMain())
                .sortOrder(image.getSortOrder())
                .url("/api/v1/vehicle-images/" + image.getId() + "/data")
                .build();
    }
}
