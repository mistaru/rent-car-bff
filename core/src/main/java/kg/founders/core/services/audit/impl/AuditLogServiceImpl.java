package kg.founders.core.services.audit.impl;

import kg.founders.core.entity.AuditLog;
import kg.founders.core.repo.AuditLogRepository;
import kg.founders.core.services.audit.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository repository;

    @Override
    @Transactional
    public AuditLog log(Long managerId, String managerLogin, String entityType, Long entityId, String action, String diffJson) {
        AuditLog entry = AuditLog.builder()
                .managerId(managerId)
                .managerLogin(managerLogin)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .diffJson(diffJson)
                .build();
        AuditLog saved = repository.save(entry);
        log.debug("Audit: manager={} ({}) {} {} id={}", managerId, managerLogin, action, entityType, entityId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getByEntity(String entityType, Long entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> filter(String managerLogin, String entityType, Long entityId,
                                 LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return repository.filter(managerLogin, entityType, entityId, from, to, pageable);
    }
}

