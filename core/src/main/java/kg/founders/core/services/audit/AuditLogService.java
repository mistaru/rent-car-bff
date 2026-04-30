package kg.founders.core.services.audit;

import kg.founders.core.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {

    /** Записать аудит-лог из кода */
    AuditLog log(Long managerId, String managerLogin, String entityType, Long entityId, String action, String diffJson);

    /** Все логи по конкретной сущности */
    List<AuditLog> getByEntity(String entityType, Long entityId);

    /** Фильтрованный список с пагинацией */
    Page<AuditLog> filter(String managerLogin, String entityType, Long entityId,
                          LocalDateTime from, LocalDateTime to, Pageable pageable);
}

