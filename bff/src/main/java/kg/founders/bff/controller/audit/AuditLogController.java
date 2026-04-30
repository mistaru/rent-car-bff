package kg.founders.bff.controller.audit;

import kg.founders.core.entity.AuditLog;
import kg.founders.core.services.audit.AuditLogService;
import kg.founders.core.settings.security.permission.annotation.ManualPermissionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * Фильтрованный список аудит-логов с пагинацией.
     * GET /api/v1/audit-logs?managerId=1&entityType=BOOKING&from=2026-01-01T00:00&to=2026-12-31T23:59&page=0&size=20
     */
    @ManualPermissionControl
    @GetMapping
    public ResponseEntity<Page<AuditLog>> filter(
            @RequestParam(required = false) String managerLogin,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                auditLogService.filter(managerLogin, entityType, entityId, from, to,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created_at"))));
    }

    /**
     * Все логи для конкретной сущности (для отображения в карточке).
     * GET /api/v1/audit-logs/entity/BOOKING/42
     */
    @ManualPermissionControl
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditLogService.getByEntity(entityType, entityId));
    }
}

