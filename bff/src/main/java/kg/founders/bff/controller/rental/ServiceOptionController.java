package kg.founders.bff.controller.rental;

import kg.founders.core.enums.AuditAction;
import kg.founders.core.model.rental.ServiceOptionDto;
import kg.founders.core.services.rental.ServiceOptionService;
import kg.founders.core.settings.audit.Auditable;
import kg.founders.core.settings.audit.AuditEntityId;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-options")
@RequiredArgsConstructor
public class ServiceOptionController {

    private final ServiceOptionService service;

    /** Все услуги (для админки) */
    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<List<ServiceOptionDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /** Только активные услуги (для клиентского каталога / checkout) */
    @ManualPermissionControl
    @GetMapping("/active")
    public ResponseEntity<List<ServiceOptionDto>> getActive() {
        return ResponseEntity.ok(service.getActive());
    }

    @ManualPermissionControl
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOptionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @ManualPermissionControl
    @Auditable(entity = "SERVICE_OPTION", action = AuditAction.CREATE)
    @PostMapping
    public ResponseEntity<ServiceOptionDto> create(@RequestBody ServiceOptionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @ManualPermissionControl
    @Auditable(entity = "SERVICE_OPTION", action = AuditAction.UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceOptionDto> update(@AuditEntityId @PathVariable Long id, @RequestBody ServiceOptionDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @ManualPermissionControl
    @Auditable(entity = "SERVICE_OPTION", action = AuditAction.DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuditEntityId @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

