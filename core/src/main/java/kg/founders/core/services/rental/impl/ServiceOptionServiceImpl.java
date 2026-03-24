package kg.founders.core.services.rental.impl;

import kg.founders.core.entity.rental.ServiceOption;
import kg.founders.core.enums.AddOnType;
import kg.founders.core.exceptions.BadRequestException;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.ServiceOptionDto;
import kg.founders.core.repo.BookingRepository;
import kg.founders.core.repo.ServiceOptionRepository;
import kg.founders.core.services.rental.ServiceOptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceOptionServiceImpl implements ServiceOptionService {

    private final ServiceOptionRepository repository;
    private final BookingRepository bookingRepository;

    /** Все услуги (для админки) — с расчётом доступности */
    @Transactional(readOnly = true)
    @Override
    public List<ServiceOptionDto> getAll() {
        return repository.findAllByOrderBySortOrderAscNameAsc().stream()
                .map(this::toDtoWithInventory)
                .collect(Collectors.toList());
    }

    /** Только активные (для клиентского каталога) — с расчётом доступности */
    @Transactional(readOnly = true)
    @Override
    public List<ServiceOptionDto> getActive() {
        return repository.findByActiveTrueOrderBySortOrderAscNameAsc().stream()
                .map(this::toDtoWithInventory)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ServiceOptionDto getById(Long id) {
        return toDtoWithInventory(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service option not found with id: " + id)));
    }

    @Transactional
    @Override
    public ServiceOptionDto create(ServiceOptionDto dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Service option with code '" + dto.getCode() + "' already exists");
        }
        ServiceOption entity = toEntity(dto);
        entity = repository.save(entity);
        log.info("Created service option: {} ({})", entity.getName(), entity.getCode());
        return toDto(entity);
    }

    @Transactional
    @Override
    public ServiceOptionDto update(Long id, ServiceOptionDto dto) {
        ServiceOption entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service option not found with id: " + id));

        // Если код меняется, проверяем уникальность
        if (!entity.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Service option with code '" + dto.getCode() + "' already exists");
        }

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setIcon(dto.getIcon());
        entity.setPricePerDay(dto.getPricePerDay());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setTotalQuantity(dto.getTotalQuantity());

        entity = repository.save(entity);
        log.info("Updated service option id={}: {} ({})", id, entity.getName(), entity.getCode());
        return toDto(entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Service option not found with id: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted service option id={}", id);
    }

    private ServiceOptionDto toDto(ServiceOption e) {
        return ServiceOptionDto.builder()
                .id(e.getId())
                .code(e.getCode())
                .name(e.getName())
                .description(e.getDescription())
                .category(e.getCategory())
                .icon(e.getIcon())
                .pricePerDay(e.getPricePerDay())
                .active(e.getActive())
                .sortOrder(e.getSortOrder())
                .totalQuantity(e.getTotalQuantity())
                .hasInventoryLimit(e.getTotalQuantity() != null)
                .build();
    }

    /** DTO с расчётом доступного количества (для списков) */
    private ServiceOptionDto toDtoWithInventory(ServiceOption e) {
        ServiceOptionDto dto = toDto(e);
        if (e.getTotalQuantity() != null) {
            try {
                AddOnType addOnType = AddOnType.valueOf(e.getCode());
                long used = bookingRepository.countActiveAddOnUsage(addOnType, LocalDate.now());
                int available = Math.max(0, e.getTotalQuantity() - (int) used);
                dto.setAvailableQuantity(available);
            } catch (IllegalArgumentException ex) {
                // code doesn't match AddOnType enum — skip
                dto.setAvailableQuantity(e.getTotalQuantity());
            }
        }
        return dto;
    }

    private ServiceOption toEntity(ServiceOptionDto dto) {
        return ServiceOption.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .icon(dto.getIcon())
                .pricePerDay(dto.getPricePerDay())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .totalQuantity(dto.getTotalQuantity())
                .build();
    }
}

