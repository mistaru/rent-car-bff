package kg.founders.core.services.rental;

import kg.founders.core.model.rental.VehicleDto;
import kg.founders.core.model.rental.VehicleSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VehicleService {
    @Transactional(readOnly = true)
    Page<VehicleDto> searchVehicles(VehicleSearchRequest request);

    @Transactional(readOnly = true)
    VehicleDto getVehicleById(Long id);

    @Transactional(readOnly = true)
    List<VehicleDto> getAllVehicles();

    @Transactional
    VehicleDto createVehicle(VehicleDto dto);

    @Transactional
    VehicleDto updateVehicle(Long id, VehicleDto dto);

    @Transactional
    void deleteVehicle(Long id);
}
