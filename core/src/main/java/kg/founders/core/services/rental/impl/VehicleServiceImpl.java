package kg.founders.core.services.rental.impl;

import kg.founders.core.converter.rental.VehicleConverter;
import kg.founders.core.data_access_layer.VehicleSpecifications;
import kg.founders.core.entity.rental.Location;
import kg.founders.core.entity.rental.PricingTemplate;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.enums.VehicleStatus;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.VehicleDto;
import kg.founders.core.model.rental.VehicleSearchRequest;
import kg.founders.core.repo.VehicleRepository;
import kg.founders.core.repo.LocationRepository;
import kg.founders.core.repo.PricingTemplateRepository;
import kg.founders.core.services.rental.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;
    private final PricingTemplateRepository pricingTemplateRepository;
    private final VehicleAttributeServiceImpl vehicleAttributeService;
    private final VehicleConverter vehicleConverter;

    @Transactional(readOnly = true)
    @Override
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
                .map(vehicleConverter::convertFromEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
        VehicleDto dto = vehicleConverter.convertFromEntity(vehicle);
        // Enrich with dynamic attributes
        dto.setAttributes(vehicleAttributeService.getVehicleAttributeValues(id));
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<VehicleDto> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll(Sort.by("id").descending());
        List<VehicleDto> vehicleDtos = vehicles.stream().map(vehicleConverter::convertFromEntity).toList();
        // Enrich with dynamic attributes
        vehicleDtos.forEach(vehicleDto ->
                vehicleDto.setAttributes(vehicleAttributeService.getVehicleAttributeValues(vehicleDto.getId())));
        return vehicleDtos;
    }

    @Transactional
    @Override
    public VehicleDto createVehicle(VehicleDto dto) {
        Location location = locationRepository.findById(
                dto.getLocationId() != null ? dto.getLocationId() : 1L
        ).orElseThrow(() -> new NotFoundException("Location not found"));

        Vehicle vehicle = Vehicle.builder()
                .brand(dto.getBrand())
                .model(dto.getModel())
                .licensePlate(dto.getLicensePlate())
                .image(dto.getImage())
                .carClass(dto.getCarClass())
                .pricePerDay(dto.getPricePerDay())
                .minPricePerDay(dto.getMinPricePerDay())
                .status(VehicleStatus.AVAILABLE)
                .location(location)
                .build();

        if (dto.getPricingTemplateId() != null) {
            PricingTemplate pt = pricingTemplateRepository.findById(dto.getPricingTemplateId()).orElse(null);
            vehicle.setPricingTemplate(pt);
        }

        vehicle = vehicleRepository.save(vehicle);

        // Save vehicle attribute values
        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            vehicleAttributeService.setVehicleAttributes(vehicle.getId(), dto.getAttributes());
        }

        log.info("Created vehicle id={} brand={} model={}", vehicle.getId(), vehicle.getBrand(), vehicle.getModel());
        return vehicleConverter.convertFromEntity(vehicle);
    }

    @Transactional
    @Override
    public VehicleDto updateVehicle(Long id, VehicleDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));

        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        if (dto.getLicensePlate() != null) vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setImage(dto.getImage());
        vehicle.setCarClass(dto.getCarClass());
        vehicle.setPricePerDay(dto.getPricePerDay());
        if (dto.getMinPricePerDay() != null) vehicle.setMinPricePerDay(dto.getMinPricePerDay());

        if (dto.getStatus() != null) {
            try {
                vehicle.setStatus(VehicleStatus.valueOf(dto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new NotFoundException("Location not found"));
            vehicle.setLocation(location);
        }

        if (dto.getPricingTemplateId() != null) {
            PricingTemplate pt = pricingTemplateRepository.findById(dto.getPricingTemplateId()).orElse(null);
            vehicle.setPricingTemplate(pt);
        }

        vehicle = vehicleRepository.save(vehicle);

        // Update vehicle attribute values
        if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
            vehicleAttributeService.setVehicleAttributes(vehicle.getId(), dto.getAttributes());
        }

        log.info("Updated vehicle id={}", vehicle.getId());
        return vehicleConverter.convertFromEntity(vehicle);
    }

    @Transactional
    @Override
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new NotFoundException("Vehicle not found with id: " + id);
        }
        vehicleAttributeService.deleteAttributeValueByVehicleId(id);
        vehicleRepository.deleteById(id);
        log.info("Deleted vehicle id={}", id);
    }
}
