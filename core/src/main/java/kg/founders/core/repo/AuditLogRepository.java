package kg.founders.core.repo;

import kg.founders.core.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, Long entityId, Pageable pageable);

    Page<AuditLog> findByManagerIdOrderByCreatedAtDesc(Long managerId, Pageable pageable);

    Page<AuditLog> findByEntityTypeOrderByCreatedAtDesc(String entityType, Pageable pageable);

    @Query(value = "SELECT * FROM audit_logs a WHERE "
            + "(CAST(:managerLogin AS VARCHAR) IS NULL OR LOWER(a.manager_login) LIKE LOWER(CONCAT('%', CAST(:managerLogin AS VARCHAR), '%'))) "
            + "AND (CAST(:entityType AS VARCHAR) IS NULL OR a.entity_type = :entityType) "
            + "AND (CAST(:entityId AS BIGINT) IS NULL OR a.entity_id = :entityId) "
            + "AND (CAST(:from AS TIMESTAMP) IS NULL OR a.created_at >= :from) "
            + "AND (CAST(:to AS TIMESTAMP) IS NULL OR a.created_at <= :to) ",
            countQuery = "SELECT COUNT(*) FROM audit_logs a WHERE "
            + "(CAST(:managerLogin AS VARCHAR) IS NULL OR LOWER(a.manager_login) LIKE LOWER(CONCAT('%', CAST(:managerLogin AS VARCHAR), '%'))) "
            + "AND (CAST(:entityType AS VARCHAR) IS NULL OR a.entity_type = :entityType) "
            + "AND (CAST(:entityId AS BIGINT) IS NULL OR a.entity_id = :entityId) "
            + "AND (CAST(:from AS TIMESTAMP) IS NULL OR a.created_at >= :from) "
            + "AND (CAST(:to AS TIMESTAMP) IS NULL OR a.created_at <= :to)",
            nativeQuery = true)
    Page<AuditLog> filter(
            @Param("managerLogin") String managerLogin,
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
}

