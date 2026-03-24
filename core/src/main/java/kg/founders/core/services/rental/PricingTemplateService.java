package kg.founders.core.services.rental;

import kg.founders.core.model.rental.CreatePricingTemplateRequest;
import kg.founders.core.model.rental.PricingTemplateDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PricingTemplateService {
    @Transactional(readOnly = true)
    List<PricingTemplateDto> getAllTemplates();

    @Transactional(readOnly = true)
    List<PricingTemplateDto> getActiveTemplates();

    @Transactional(readOnly = true)
    PricingTemplateDto getTemplateById(Long id);

    @Transactional
    PricingTemplateDto createTemplate(CreatePricingTemplateRequest request);

    @Transactional
    PricingTemplateDto updateTemplate(Long id, CreatePricingTemplateRequest request);

    @Transactional
    void deleteTemplate(Long id);

    @Transactional
    PricingTemplateDto toggleActive(Long id);

    @Transactional
    void assignTemplateToVehicle(Long vehicleId, Long templateId);
}
