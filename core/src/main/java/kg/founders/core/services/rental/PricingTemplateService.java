package kg.founders.core.services.rental;

import kg.founders.core.converter.RentalMapper;
import kg.founders.core.entity.rental.PriceTier;
import kg.founders.core.entity.rental.PricingTemplate;
import kg.founders.core.entity.rental.Vehicle;
import kg.founders.core.exceptions.BadRequestException;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.CreatePricingTemplateRequest;
import kg.founders.core.model.rental.PriceTierDto;
import kg.founders.core.model.rental.PricingTemplateDto;
import kg.founders.core.repo.PricingTemplateRepository;
import kg.founders.core.repo.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingTemplateService {

    private final PricingTemplateRepository templateRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalMapper rentalMapper;

    @Transactional(readOnly = true)
    public List<PricingTemplateDto> getAllTemplates() {
        return templateRepository.findAllWithTiers().stream()
                .map(rentalMapper::toPricingTemplateDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PricingTemplateDto> getActiveTemplates() {
        return templateRepository.findAllActiveWithTiers().stream()
                .map(rentalMapper::toPricingTemplateDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PricingTemplateDto getTemplateById(Long id) {
        PricingTemplate template = templateRepository.findByIdWithTiers(id)
                .orElseThrow(() -> new NotFoundException("Pricing template not found with id: " + id));
        return rentalMapper.toPricingTemplateDto(template);
    }

    @Transactional
    public PricingTemplateDto createTemplate(CreatePricingTemplateRequest request) {
        log.info("Creating pricing template: {}", request.getName());

        if (templateRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Pricing template with name '" + request.getName() + "' already exists");
        }

        validateTiers(request.getTiers());

        PricingTemplate template = PricingTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .active(true)
                .tiers(new ArrayList<>())
                .build();

        for (PriceTierDto tierDto : request.getTiers()) {
            PriceTier tier = PriceTier.builder()
                    .pricingTemplate(template)
                    .minDays(tierDto.getMinDays())
                    .maxDays(tierDto.getMaxDays())
                    .pricePerDay(tierDto.getPricePerDay())
                    .build();
            template.getTiers().add(tier);
        }

        template = templateRepository.save(template);
        log.info("Pricing template created with id: {}", template.getId());

        return rentalMapper.toPricingTemplateDto(template);
    }

    @Transactional
    public PricingTemplateDto updateTemplate(Long id, CreatePricingTemplateRequest request) {
        log.info("Updating pricing template id: {}", id);

        PricingTemplate template = templateRepository.findByIdWithTiers(id)
                .orElseThrow(() -> new NotFoundException("Pricing template not found with id: " + id));

        validateTiers(request.getTiers());

        template.setName(request.getName());
        template.setDescription(request.getDescription());
        if (request.getCurrency() != null) {
            template.setCurrency(request.getCurrency());
        }

        // Очищаем старые тарифы и добавляем новые
        template.getTiers().clear();
        for (PriceTierDto tierDto : request.getTiers()) {
            PriceTier tier = PriceTier.builder()
                    .pricingTemplate(template)
                    .minDays(tierDto.getMinDays())
                    .maxDays(tierDto.getMaxDays())
                    .pricePerDay(tierDto.getPricePerDay())
                    .build();
            template.getTiers().add(tier);
        }

        template = templateRepository.save(template);

        // Обновляем minPricePerDay у всех машин с этим шаблоном
        updateVehiclesMinPrice(template);

        log.info("Pricing template {} updated", id);
        return rentalMapper.toPricingTemplateDto(template);
    }

    @Transactional
    public void deleteTemplate(Long id) {
        PricingTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pricing template not found with id: " + id));

        // Проверяем, что шаблон не используется
        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getPricingTemplate() != null && v.getPricingTemplate().getId().equals(id))
                .collect(Collectors.toList());
        if (!vehicles.isEmpty()) {
            throw new BadRequestException(
                    "Cannot delete template: it is assigned to " + vehicles.size() + " vehicle(s). " +
                    "Reassign them first or deactivate the template.");
        }

        templateRepository.delete(template);
        log.info("Pricing template {} deleted", id);
    }

    @Transactional
    public PricingTemplateDto toggleActive(Long id) {
        PricingTemplate template = templateRepository.findByIdWithTiers(id)
                .orElseThrow(() -> new NotFoundException("Pricing template not found with id: " + id));
        template.setActive(!template.getActive());
        template = templateRepository.save(template);
        log.info("Pricing template {} active status set to {}", id, template.getActive());
        return rentalMapper.toPricingTemplateDto(template);
    }

    @Transactional
    public void assignTemplateToVehicle(Long vehicleId, Long templateId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + vehicleId));
        PricingTemplate template = templateRepository.findByIdWithTiers(templateId)
                .orElseThrow(() -> new NotFoundException("Pricing template not found with id: " + templateId));

        vehicle.setPricingTemplate(template);

        // Вычисляем и кэшируем minPricePerDay
        BigDecimal minPrice = template.getTiers().stream()
                .map(PriceTier::getPricePerDay)
                .min(BigDecimal::compareTo)
                .orElse(vehicle.getPricePerDay());
        vehicle.setMinPricePerDay(minPrice);

        vehicleRepository.save(vehicle);
        log.info("Vehicle {} assigned to pricing template {} (minPrice={})", vehicleId, templateId, minPrice);
    }

    // =================== ВАЛИДАЦИЯ ТАРИФОВ ===================

    /**
     * Валидирует корректность набора тарифных диапазонов:
     * 1. Минимум 1 тариф
     * 2. Первый диапазон начинается с 1
     * 3. Нет пересечений
     * 4. Нет разрывов
     * 5. Последний диапазон maxDays == null (без ограничения)
     * 6. pricePerDay > 0
     */
    private void validateTiers(List<PriceTierDto> tiers) {
        if (tiers == null || tiers.isEmpty()) {
            throw new BadRequestException("At least one price tier is required");
        }

        // Сортируем по minDays
        List<PriceTierDto> sorted = tiers.stream()
                .sorted(Comparator.comparing(PriceTierDto::getMinDays))
                .collect(Collectors.toList());

        // Первый должен начинаться с 1
        if (sorted.get(0).getMinDays() != 1) {
            throw new BadRequestException("First price tier must start with minDays = 1");
        }

        // Последний должен быть без ограничения (maxDays == null)
        PriceTierDto lastTier = sorted.get(sorted.size() - 1);
        if (lastTier.getMaxDays() != null) {
            throw new BadRequestException("Last price tier must have maxDays = null (unlimited)");
        }

        for (int i = 0; i < sorted.size(); i++) {
            PriceTierDto tier = sorted.get(i);

            // Цена должна быть > 0
            if (tier.getPricePerDay() == null || tier.getPricePerDay().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Price per day must be greater than 0 for tier starting at day " + tier.getMinDays());
            }

            // minDays > 0
            if (tier.getMinDays() == null || tier.getMinDays() < 1) {
                throw new BadRequestException("minDays must be >= 1");
            }

            // maxDays >= minDays (если не null)
            if (tier.getMaxDays() != null && tier.getMaxDays() < tier.getMinDays()) {
                throw new BadRequestException("maxDays must be >= minDays for tier starting at day " + tier.getMinDays());
            }

            // Проверяем непрерывность: maxDays[i] + 1 == minDays[i+1]
            if (i < sorted.size() - 1) {
                if (tier.getMaxDays() == null) {
                    throw new BadRequestException("Only the last price tier can have maxDays = null");
                }
                PriceTierDto next = sorted.get(i + 1);
                if (tier.getMaxDays() + 1 != next.getMinDays()) {
                    throw new BadRequestException(
                            "Gap or overlap between tiers: tier ending at day " + tier.getMaxDays()
                            + " and tier starting at day " + next.getMinDays()
                            + ". Expected next tier to start at day " + (tier.getMaxDays() + 1));
                }
            }
        }
    }

    private void updateVehiclesMinPrice(PricingTemplate template) {
        BigDecimal minPrice = template.getTiers().stream()
                .map(PriceTier::getPricePerDay)
                .min(BigDecimal::compareTo)
                .orElse(null);

        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getPricingTemplate() != null && v.getPricingTemplate().getId().equals(template.getId()))
                .collect(Collectors.toList());

        for (Vehicle vehicle : vehicles) {
            vehicle.setMinPricePerDay(minPrice);
            vehicleRepository.save(vehicle);
        }

        if (!vehicles.isEmpty()) {
            log.info("Updated minPricePerDay={} for {} vehicles with template {}",
                    minPrice, vehicles.size(), template.getId());
        }
    }
}

