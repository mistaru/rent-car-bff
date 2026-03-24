package kg.founders.core.services.rental.impl;

import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.entity.rental.VehicleBlockedPeriod;
import kg.founders.core.exceptions.BadRequestException;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.CreateBlockedPeriodRequest;
import kg.founders.core.model.rental.VehicleBlockedPeriodDto;
import kg.founders.core.repo.VehicleBlockedPeriodRepository;
import kg.founders.core.repo.VehicleRepository;
import kg.founders.core.services.rental.VehicleBlockedPeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleBlockedPeriodServiceImpl implements VehicleBlockedPeriodService {

    private final VehicleBlockedPeriodRepository blockedPeriodRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    @Override
    public List<VehicleBlockedPeriodDto> getAll() {
        return blockedPeriodRepository.findAllWithVehicle().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<VehicleBlockedPeriodDto> getByVehicleId(Long vehicleId) {
        return blockedPeriodRepository.findByVehicleId(vehicleId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public VehicleBlockedPeriodDto create(CreateBlockedPeriodRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + request.getVehicleId()));

        // Проверка пересечения с другими блокировками
        if (blockedPeriodRepository.existsOverlappingBlock(
                request.getVehicleId(), request.getStartDate(), request.getEndDate())) {
            throw new BadRequestException("Blocked period overlaps with an existing block for this vehicle");
        }

        VehicleBlockedPeriod period = VehicleBlockedPeriod.builder()
                .vehicle(vehicle)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .build();

        period = blockedPeriodRepository.save(period);
        log.info("Created blocked period id={} for vehicle {} ({} - {})",
                period.getId(), vehicle.getId(), request.getStartDate(), request.getEndDate());

        return toDto(period);
    }

    @Transactional
    @Override
    public VehicleBlockedPeriodDto update(Long id, CreateBlockedPeriodRequest request) {
        VehicleBlockedPeriod period = blockedPeriodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Blocked period not found with id: " + id));

        validateDates(request.getStartDate(), request.getEndDate());

        // Проверка пересечения (исключая текущий период)
        if (blockedPeriodRepository.existsOverlappingBlockExcluding(
                period.getVehicle().getId(), id, request.getStartDate(), request.getEndDate())) {
            throw new BadRequestException("Updated period overlaps with an existing block");
        }

        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setReason(request.getReason());

        period = blockedPeriodRepository.save(period);
        log.info("Updated blocked period id={}", id);

        return toDto(period);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!blockedPeriodRepository.existsById(id)) {
            throw new NotFoundException("Blocked period not found with id: " + id);
        }
        blockedPeriodRepository.deleteById(id);
        log.info("Deleted blocked period id={}", id);
    }

    private void validateDates(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new BadRequestException("End date must be after start date");
        }
    }

    private VehicleBlockedPeriodDto toDto(VehicleBlockedPeriod period) {
        Vehicle v = period.getVehicle();
        return VehicleBlockedPeriodDto.builder()
                .id(period.getId())
                .vehicleId(v.getId())
                .vehicleName(v.getBrand() + " " + v.getModel())
                .startDate(period.getStartDate())
                .endDate(period.getEndDate())
                .reason(period.getReason())
                .createdAt(period.getCreatedAt())
                .build();
    }
}

