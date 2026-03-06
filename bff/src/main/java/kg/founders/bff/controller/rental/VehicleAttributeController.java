package kg.founders.bff.controller.rental;

import kg.founders.core.model.rental.VehicleAttributeDto;
import kg.founders.core.services.rental.VehicleAttributeService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicle-attributes")
@RequiredArgsConstructor
public class VehicleAttributeController {

    private final VehicleAttributeService service;

    // ===================== CRUD справочника атрибутов =====================

    /** Все атрибуты (для админки) */
    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<List<VehicleAttributeDto>> getAll() {
        return ResponseEntity.ok(service.getAllAttributes());
    }

    @ManualPermissionControl
    @GetMapping("/{id}")
    public ResponseEntity<VehicleAttributeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAttributeById(id));
    }

    @ManualPermissionControl
    @PostMapping
    public ResponseEntity<VehicleAttributeDto> create(@RequestBody VehicleAttributeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAttribute(dto));
    }

    @ManualPermissionControl
    @PutMapping("/{id}")
    public ResponseEntity<VehicleAttributeDto> update(@PathVariable Long id, @RequestBody VehicleAttributeDto dto) {
        return ResponseEntity.ok(service.updateAttribute(id, dto));
    }

    @ManualPermissionControl
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== Фильтры для каталога =====================

    /** Возвращает filterable-атрибуты с используемыми значениями и рекомендованным типом UI */
    @ManualPermissionControl
    @GetMapping("/filters")
    public ResponseEntity<List<VehicleAttributeDto>> getFilterableAttributes() {
        return ResponseEntity.ok(service.getFilterableAttributes());
    }

    // ===================== Значения для конкретного авто =====================

    /** Получить все значения атрибутов для авто */
    @ManualPermissionControl
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Map<String, String>> getVehicleValues(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(service.getVehicleAttributeValues(vehicleId));
    }

    /** Установить значения атрибутов для авто (bulk) */
    @ManualPermissionControl
    @PutMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Void> setVehicleValues(@PathVariable Long vehicleId,
                                                   @RequestBody Map<String, String> attributes) {
        service.setVehicleAttributes(vehicleId, attributes);
        return ResponseEntity.ok().build();
    }
}

