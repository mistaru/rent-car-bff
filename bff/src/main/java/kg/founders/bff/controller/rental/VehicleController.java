package kg.founders.bff.controller.rental;

import kg.founders.core.enums.AuditAction;
import kg.founders.core.model.rental.VehicleDto;
import kg.founders.core.settings.audit.Auditable;
import kg.founders.core.settings.audit.AuditEntityId;
import kg.founders.core.model.rental.VehicleSearchRequest;
import kg.founders.core.services.rental.AvailabilityService;
import kg.founders.core.services.rental.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final AvailabilityService availabilityService;

    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<Page<VehicleDto>> searchVehicles(
            @RequestParam(required = false) String locationId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String carClass,
            @RequestParam(required = false) String drivetrain,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String pickupDate,
            @RequestParam(required = false) String dropoffDate,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String size,
            @RequestParam Map<String, String> allParams) {

        Integer parsedPage = parseInt(page);
        Integer parsedSize = parseInt(size);

        // Extract dynamic attribute filters (params starting with "attr_")
        Map<String, String> attrFilters = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("attr_") && entry.getValue() != null && !entry.getValue().isBlank()) {
                attrFilters.put(entry.getKey().substring(5), entry.getValue());
            }
        }

        VehicleSearchRequest request = VehicleSearchRequest.builder()
                .locationId(parseLong(locationId))
                .brand(blankToNull(brand))
                .carClass(blankToNull(carClass))
                .drivetrain(blankToNull(drivetrain))
                .fuelType(blankToNull(fuelType))
                .minPrice(parseDecimal(minPrice))
                .maxPrice(parseDecimal(maxPrice))
                .pickupDate(parseDate(pickupDate))
                .dropoffDate(parseDate(dropoffDate))
                .attributeFilters(attrFilters.isEmpty() ? null : attrFilters)
                .page(parsedPage != null ? parsedPage : 0)
                .size(parsedSize != null ? parsedSize : 20)
                .build();

        return ResponseEntity.ok(vehicleService.searchVehicles(request));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank() || "null".equalsIgnoreCase(s)) ? null : s.trim();
    }

    private static Long parseLong(String s) {
        String v = blankToNull(s);
        if (v == null) return null;
        try { return Long.valueOf(v); } catch (NumberFormatException e) { return null; }
    }

    private static Integer parseInt(String s) {
        String v = blankToNull(s);
        if (v == null) return null;
        try { return Integer.valueOf(v); } catch (NumberFormatException e) { return null; }
    }

    private static BigDecimal parseDecimal(String s) {
        String v = blankToNull(s);
        if (v == null) return null;
        try { return new BigDecimal(v); } catch (NumberFormatException e) { return null; }
    }

    private static LocalDate parseDate(String s) {
        String v = blankToNull(s);
        if (v == null) return null;
        try { return LocalDate.parse(v); } catch (Exception e) { return null; }
    }

    @ManualPermissionControl
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicle(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @ManualPermissionControl
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dropoffDate) {
        return ResponseEntity.ok(availabilityService.isVehicleAvailable(id, pickupDate, dropoffDate));
    }

    @ManualPermissionControl
    @GetMapping("/all")
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @ManualPermissionControl
    @Auditable(entity = "VEHICLE", action = AuditAction.CREATE)
    @PostMapping
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody VehicleDto dto) {
        return ResponseEntity.ok(vehicleService.createVehicle(dto));
    }

    @ManualPermissionControl
    @Auditable(entity = "VEHICLE", action = AuditAction.UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDto> updateVehicle(@AuditEntityId @PathVariable Long id, @RequestBody VehicleDto dto) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, dto));
    }

    @ManualPermissionControl
    @Auditable(entity = "VEHICLE", action = AuditAction.DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@AuditEntityId @PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
