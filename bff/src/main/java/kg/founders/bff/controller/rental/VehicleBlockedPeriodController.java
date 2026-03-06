package kg.founders.bff.controller.rental;

import kg.founders.core.model.rental.CreateBlockedPeriodRequest;
import kg.founders.core.model.rental.VehicleBlockedPeriodDto;
import kg.founders.core.services.rental.VehicleBlockedPeriodService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles/blocked-periods")
@RequiredArgsConstructor
public class VehicleBlockedPeriodController {

    private final VehicleBlockedPeriodService service;

    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<List<VehicleBlockedPeriodDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @ManualPermissionControl
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleBlockedPeriodDto>> getByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(service.getByVehicleId(vehicleId));
    }

    @ManualPermissionControl
    @PostMapping
    public ResponseEntity<VehicleBlockedPeriodDto> create(@Valid @RequestBody CreateBlockedPeriodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @ManualPermissionControl
    @PutMapping("/{id}")
    public ResponseEntity<VehicleBlockedPeriodDto> update(@PathVariable Long id,
                                                           @Valid @RequestBody CreateBlockedPeriodRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @ManualPermissionControl
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

