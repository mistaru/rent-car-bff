package kg.founders.core.services.rental;

import kg.founders.core.model.rental.LocationDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LocationService {
    @Transactional(readOnly = true)
    List<LocationDto> getAllLocations();

    @Transactional(readOnly = true)
    LocationDto getLocationById(Long id);
}
