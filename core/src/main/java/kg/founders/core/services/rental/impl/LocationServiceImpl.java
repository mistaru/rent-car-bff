package kg.founders.core.services.rental.impl;

import kg.founders.core.converter.rental.LocationConverter;
import kg.founders.core.entity.rental.Location;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.LocationDto;
import kg.founders.core.repo.LocationRepository;
import kg.founders.core.services.rental.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationConverter locationConverter;

    @Transactional(readOnly = true)
    @Override
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationConverter::convertFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public LocationDto getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found with id: " + id));
        return locationConverter.convertFromEntity(location);
    }
}
