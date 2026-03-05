package kg.founders.bff.controller.rental;

import kg.founders.core.model.rental.CreatePricingTemplateRequest;
import kg.founders.core.model.rental.PricingTemplateDto;
import kg.founders.core.services.rental.PricingTemplateService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing-templates")
@RequiredArgsConstructor
public class PricingTemplateController {

    private final PricingTemplateService templateService;

    /** Получить все шаблоны тарифов */
    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<List<PricingTemplateDto>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    /** Получить только активные шаблоны */
    @ManualPermissionControl
    @GetMapping("/active")
    public ResponseEntity<List<PricingTemplateDto>> getActiveTemplates() {
        return ResponseEntity.ok(templateService.getActiveTemplates());
    }

    /** Получить шаблон по ID */
    @ManualPermissionControl
    @GetMapping("/{id}")
    public ResponseEntity<PricingTemplateDto> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    /** Создать новый шаблон тарифов */
    @ManualPermissionControl
    @PostMapping
    public ResponseEntity<PricingTemplateDto> createTemplate(
            @Valid @RequestBody CreatePricingTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request));
    }

    /** Обновить шаблон тарифов */
    @ManualPermissionControl
    @PutMapping("/{id}")
    public ResponseEntity<PricingTemplateDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody CreatePricingTemplateRequest request) {
        return ResponseEntity.ok(templateService.updateTemplate(id, request));
    }

    /** Удалить шаблон (только если не привязан к машинам) */
    @ManualPermissionControl
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    /** Включить/отключить шаблон */
    @ManualPermissionControl
    @PostMapping("/{id}/toggle-active")
    public ResponseEntity<PricingTemplateDto> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.toggleActive(id));
    }

    /** Привязать шаблон к автомобилю */
    @ManualPermissionControl
    @PostMapping("/assign")
    public ResponseEntity<Void> assignToVehicle(
            @RequestParam Long vehicleId,
            @RequestParam Long templateId) {
        templateService.assignTemplateToVehicle(vehicleId, templateId);
        return ResponseEntity.ok().build();
    }
}

