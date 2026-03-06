package kg.founders.bff.controller.rental;

import kg.founders.core.model.rental.ReportDto;
import kg.founders.core.model.rental.ReportFilterRequest;
import kg.founders.core.services.rental.ReportService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Генерация сводного отчёта с фильтрами.
     * Все параметры опциональны — без них вернётся полный отчёт.
     */
    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<ReportDto> getReport(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String bookingStatus,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String carClass) {

        ReportFilterRequest filter = ReportFilterRequest.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .bookingStatus(bookingStatus)
                .paymentStatus(paymentStatus)
                .vehicleId(vehicleId)
                .carClass(carClass)
                .build();

        return ResponseEntity.ok(reportService.generateReport(filter));
    }
}

