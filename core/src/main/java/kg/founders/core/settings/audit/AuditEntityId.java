package kg.founders.core.settings.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает параметр метода, содержащий entity ID для аудит-лога.
 * <pre>
 * {@literal @}Auditable(entity = "BOOKING", action = AuditAction.UPDATE)
 * public BookingDto updateBooking(@AuditEntityId Long id, ...) { ... }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AuditEntityId {
}

