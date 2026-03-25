package kg.founders.core.services.rental;

import kg.founders.core.model.rental.VehicleAttributeDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface VehicleAttributeService {
    @Transactional(readOnly = true)
    List<VehicleAttributeDto> getAllAttributes();

    @Transactional(readOnly = true)
    VehicleAttributeDto getAttributeById(Long id);

    @Transactional
    VehicleAttributeDto createAttribute(VehicleAttributeDto dto);

    @Transactional
    VehicleAttributeDto updateAttribute(Long id, VehicleAttributeDto dto);

    @Transactional
    void deleteAttribute(Long id);

    @Transactional
    void deleteAttributeValueByVehicleId(Long vehicleId);

    @Transactional(readOnly = true)
    List<VehicleAttributeDto> getFilterableAttributes();

    @Transactional(readOnly = true)
    Map<String, String> getVehicleAttributeValues(Long vehicleId);

    @Transactional
    void setVehicleAttributeValue(Long vehicleId, String attributeCode, String value);

    @Transactional
    void setVehicleAttributes(Long vehicleId, Map<String, String> attributes);
}
