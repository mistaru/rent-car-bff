package kg.founders.core.services.rental.impl;

import kg.founders.core.entity.rental.VehicleAttribute;
import kg.founders.core.entity.rental.VehicleAttributeValue;
import kg.founders.core.exceptions.BadRequestException;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.VehicleAttributeDto;
import kg.founders.core.repo.VehicleAttributeRepository;
import kg.founders.core.repo.VehicleAttributeValueRepository;
import kg.founders.core.repo.VehicleRepository;
import kg.founders.core.services.rental.VehicleAttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleAttributeServiceImpl implements VehicleAttributeService {

    private final VehicleAttributeRepository attributeRepository;
    private final VehicleAttributeValueRepository valueRepository;
    private final VehicleRepository vehicleRepository;

    // ===================== CRUD атрибутов (справочник) =====================

    @Transactional(readOnly = true)
    @Override
    public List<VehicleAttributeDto> getAllAttributes() {
        return attributeRepository.findAllByOrderBySortOrderAscNameAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public VehicleAttributeDto getAttributeById(Long id) {
        return toDto(attributeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attribute not found: " + id)));
    }

    @Transactional
    @Override
    public VehicleAttributeDto createAttribute(VehicleAttributeDto dto) {
        if (attributeRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Attribute with code '" + dto.getCode() + "' already exists");
        }
        validateValueType(dto.getValueType());
        VehicleAttribute entity = toEntity(dto);
        entity = attributeRepository.save(entity);
        log.info("Created vehicle attribute: {} ({})", entity.getName(), entity.getCode());
        return toDto(entity);
    }

    @Transactional
    @Override
    public VehicleAttributeDto updateAttribute(Long id, VehicleAttributeDto dto) {
        VehicleAttribute entity = attributeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attribute not found: " + id));
        if (!entity.getCode().equals(dto.getCode()) && attributeRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Attribute with code '" + dto.getCode() + "' already exists");
        }
        validateValueType(dto.getValueType());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setValueType(dto.getValueType());
        entity.setPossibleValues(dto.getPossibleValues() != null ? String.join(",", dto.getPossibleValues()) : null);
        entity.setFilterable(dto.getFilterable() != null ? dto.getFilterable() : true);
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity = attributeRepository.save(entity);
        log.info("Updated vehicle attribute id={}: {} ({})", id, entity.getName(), entity.getCode());
        return toDto(entity);
    }

    @Transactional
    @Override
    public void deleteAttribute(Long id) {
        if (!attributeRepository.existsById(id)) {
            throw new NotFoundException("Attribute not found: " + id);
        }
        attributeRepository.deleteById(id);
        log.info("Deleted vehicle attribute id={}", id);
    }

    @Transactional
    @Override
    public void deleteAttributeValueByVehicleId(Long vehicleId) {
        valueRepository.deleteByVehicleId(vehicleId);
        log.info("Deleted vehicle value attributes vehicle_id={}", vehicleId);
    }

    // ===================== Фильтры для каталога =====================

    /**
     * Возвращает filterable-атрибуты с реальными значениями из авто + рекомендованный тип UI.
     * Фронт использует это для динамического построения фильтров.
     */
    @Transactional(readOnly = true)
    @Override
    public List<VehicleAttributeDto> getFilterableAttributes() {
        List<VehicleAttribute> attrs = attributeRepository.findByActiveTrueAndFilterableTrueOrderBySortOrderAsc();
        List<VehicleAttributeDto> result = new ArrayList<>();

        for (VehicleAttribute attr : attrs) {
            List<String> usedValues = valueRepository.findDistinctValuesByAttributeId(attr.getId());

            VehicleAttributeDto dto = toDto(attr);
            dto.setUsedValues(usedValues);

            // Вычисляем тип фильтра
            if ("BOOLEAN".equals(attr.getValueType())) {
                dto.setFilterType("checkbox");
            } else if (usedValues.size() <= 1) {
                dto.setFilterType("none");
            } else if (usedValues.size() == 2) {
                dto.setFilterType("radio");
            } else {
                dto.setFilterType("select");
            }

            // Не показываем фильтр если 0-1 значение
            if (!"none".equals(dto.getFilterType())) {
                result.add(dto);
            }
        }
        return result;
    }

    // ===================== Значения для конкретного авто =====================

    @Transactional(readOnly = true)
    @Override
    public Map<String, String> getVehicleAttributeValues(Long vehicleId) {
        return valueRepository.findByVehicleIdWithAttribute(vehicleId).stream()
                .collect(Collectors.toMap(
                        v -> v.getAttribute().getCode(),
                        VehicleAttributeValue::getValue,
                        (a, b) -> a));
    }

    @Transactional
    @Override
    public void setVehicleAttributeValue(Long vehicleId, String attributeCode, String value) {
        var vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId));
        var attribute = attributeRepository.findByCode(attributeCode)
                .orElseThrow(() -> new NotFoundException("Attribute not found: " + attributeCode));

        // Валидация для ENUM
        if ("ENUM".equals(attribute.getValueType()) && attribute.getPossibleValues() != null) {
            List<String> allowed = Arrays.asList(attribute.getPossibleValues().split(","));
            if (!allowed.contains(value)) {
                throw new BadRequestException("Value '" + value + "' not allowed for attribute '" + attributeCode
                        + "'. Allowed: " + attribute.getPossibleValues());
            }
        }

        VehicleAttributeValue existing = valueRepository.findByVehicleIdAndAttributeId(vehicleId, attribute.getId());
        if (existing != null) {
            existing.setValue(value);
            valueRepository.save(existing);
        } else {
            valueRepository.save(VehicleAttributeValue.builder()
                    .vehicle(vehicle)
                    .attribute(attribute)
                    .value(value)
                    .build());
        }
        log.debug("Set attribute {}={} for vehicle {}", attributeCode, value, vehicleId);
    }

    @Transactional
    @Override
    public void setVehicleAttributes(Long vehicleId, Map<String, String> attributes) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isBlank()) {
                setVehicleAttributeValue(vehicleId, entry.getKey(), entry.getValue());
            }
        }
    }

    // ===================== Helpers =====================

    private void validateValueType(String vt) {
        if (!List.of("ENUM", "TEXT", "NUMBER", "BOOLEAN").contains(vt)) {
            throw new BadRequestException("Invalid valueType: " + vt + ". Must be ENUM, TEXT, NUMBER, or BOOLEAN");
        }
    }

    private VehicleAttributeDto toDto(VehicleAttribute e) {
        return VehicleAttributeDto.builder()
                .id(e.getId())
                .code(e.getCode())
                .name(e.getName())
                .valueType(e.getValueType())
                .possibleValues(e.getPossibleValues() != null
                        ? Arrays.asList(e.getPossibleValues().split(","))
                        : Collections.emptyList())
                .filterable(e.getFilterable())
                .sortOrder(e.getSortOrder())
                .active(e.getActive())
                .build();
    }

    private VehicleAttribute toEntity(VehicleAttributeDto dto) {
        return VehicleAttribute.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .valueType(dto.getValueType())
                .possibleValues(dto.getPossibleValues() != null ? String.join(",", dto.getPossibleValues()) : null)
                .filterable(dto.getFilterable() != null ? dto.getFilterable() : true)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }
}

