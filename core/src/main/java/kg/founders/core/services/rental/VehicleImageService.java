package kg.founders.core.services.rental;

import kg.founders.core.entity.rental.VehicleImage;
import kg.founders.core.model.rental.VehicleImageDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VehicleImageService {
    @Transactional(readOnly = true)
    List<VehicleImageDto> getImagesByVehicleId(Long vehicleId);

    @Transactional(readOnly = true)
    VehicleImage getRawById(Long id);

    @Transactional
    void delete(Long imageId);

    VehicleImageDto upload(Long vehicleId, MultipartFile file, boolean isMain) throws IOException;

    void setMain(Long id);
}
