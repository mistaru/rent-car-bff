package kg.founders.core.services.rental;

import kg.founders.core.converter.RentalMapper;
import kg.founders.core.data_access_layer.VehicleSpecifications;
import kg.founders.core.entity.Vehicle;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.VehicleDto;
import kg.founders.core.model.rental.VehicleSearchRequest;
import kg.founders.core.repo.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RentalMapper rentalMapper;

    @Transactional(readOnly = true)
    public Page<VehicleDto> searchVehicles(VehicleSearchRequest request) {
        log.info("Searching vehicles with filters: location={}, brand={}, minPrice={}, maxPrice={}",
                request.getLocationId(), request.getBrand(), request.getMinPrice(), request.getMaxPrice());

        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by("pricePerDay").ascending()
        );

        return vehicleRepository
                .findAll(VehicleSpecifications.fromSearchRequest(request), pageRequest)
                .map(rentalMapper::toVehicleDto);
    }

    @Transactional(readOnly = true)
    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
        return rentalMapper.toVehicleDto(vehicle);
    }
}
