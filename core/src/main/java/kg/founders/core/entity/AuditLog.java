package kg.founders.core.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_manager", columnList = "manager_id"),
        @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_audit_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /** ID менеджера (Auth.id) */
    @Column(name = "manager_id", nullable = false)
    Long managerId;

    /** Логин менеджера */
    @Column(name = "manager_login", length = 100)
    String managerLogin;

    /** Тип сущности: BOOKING, VEHICLE, PAYMENT и т.д. */
    @Column(name = "entity_type", nullable = false, length = 50)
    String entityType;

    /** ID изменённой сущности */
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    /** Действие: CREATE, UPDATE, DELETE, STATUS_CHANGE и т.д. */
    @Column(nullable = false, length = 50)
    String action;

    /** JSON-diff старое/новое значение */
    @Column(name = "diff_json", columnDefinition = "TEXT")
    String diffJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}

