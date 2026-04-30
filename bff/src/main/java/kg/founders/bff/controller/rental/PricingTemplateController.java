package kg.founders.bff.controller.rental;

import kg.founders.core.enums.AuditAction;
import kg.founders.core.model.rental.CreatePricingTemplateRequest;
import kg.founders.core.model.rental.PricingTemplateDto;
import kg.founders.core.services.rental.impl.PricingTemplateServiceImpl;
import kg.founders.core.settings.audit.Auditable;
import kg.founders.core.settings.audit.AuditEntityId;
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

    private final PricingTemplateServiceImpl templateService;

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
    @Auditable(entity = "PRICING_TEMPLATE", action = AuditAction.CREATE)
    @PostMapping
    public ResponseEntity<PricingTemplateDto> createTemplate(
            @Valid @RequestBody CreatePricingTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.createTemplate(request));
    }

    /** Обновить шаблон тарифов */
    @ManualPermissionControl
    @Auditable(entity = "PRICING_TEMPLATE", action = AuditAction.UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<PricingTemplateDto> updateTemplate(
            @AuditEntityId @PathVariable Long id,
            @Valid @RequestBody CreatePricingTemplateRequest request) {
        return ResponseEntity.ok(templateService.updateTemplate(id, request));
    }

    /** Удалить шаблон (только если не привязан к машинам) */
    @ManualPermissionControl
    @Auditable(entity = "PRICING_TEMPLATE", action = AuditAction.DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@AuditEntityId @PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    /** Включить/отключить шаблон */
    @ManualPermissionControl
    @Auditable(entity = "PRICING_TEMPLATE", action = AuditAction.STATUS_CHANGE)
    @PostMapping("/{id}/toggle-active")
    public ResponseEntity<PricingTemplateDto> toggleActive(@AuditEntityId @PathVariable Long id) {
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

