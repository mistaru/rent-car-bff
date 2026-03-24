package kg.founders.core.services.rental;

import kg.founders.core.model.rental.CreateBlockedPeriodRequest;
import kg.founders.core.model.rental.VehicleBlockedPeriodDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VehicleBlockedPeriodService {
    @Transactional(readOnly = true)
    List<VehicleBlockedPeriodDto> getAll();

    @Transactional(readOnly = true)
    List<VehicleBlockedPeriodDto> getByVehicleId(Long vehicleId);

    @Transactional
    VehicleBlockedPeriodDto create(CreateBlockedPeriodRequest request);

    @Transactional
    VehicleBlockedPeriodDto update(Long id, CreateBlockedPeriodRequest request);

    @Transactional
    void delete(Long id);
}
