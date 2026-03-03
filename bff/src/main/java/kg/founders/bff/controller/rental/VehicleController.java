package kg.founders.bff.controller.rental;

import kg.founders.core.model.rental.VehicleDto;
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
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String yearFrom,
            @RequestParam(required = false) String yearTo,
            @RequestParam(required = false) String pickupDate,
            @RequestParam(required = false) String dropoffDate,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String size) {

        Integer parsedPage = parseInt(page);
        Integer parsedSize = parseInt(size);

        VehicleSearchRequest request = VehicleSearchRequest.builder()
                .locationId(parseLong(locationId))
                .brand(blankToNull(brand))
                .carClass(blankToNull(carClass))
                .drivetrain(blankToNull(drivetrain))
                .fuelType(blankToNull(fuelType))
                .minPrice(parseDecimal(minPrice))
                .maxPrice(parseDecimal(maxPrice))
                .year(parseInt(year))
                .yearFrom(parseInt(yearFrom))
                .yearTo(parseInt(yearTo))
                .pickupDate(parseDate(pickupDate))
                .dropoffDate(parseDate(dropoffDate))
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
}
