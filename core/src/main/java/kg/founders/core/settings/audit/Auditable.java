package kg.founders.core.settings.audit;

import kg.founders.core.enums.AuditAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для автоматического аудит-логирования методов.
 * <p>
 * Помечает метод контроллера или сервиса — при его вызове AOP-аспект
 * автоматически запишет запись в таблицу {@code audit_logs}.
 * <p>
 * Пример использования:
 * <pre>
 * {@literal @}Auditable(entity = "BOOKING", action = AuditAction.CREATE)
 * public BookingDto createBooking(CreateBookingRequest request) { ... }
 *
 * // entityId берётся из параметра с @AuditEntityId или из поля id результата
 * {@literal @}Auditable(entity = "BOOKING", action = AuditAction.UPDATE)
 * public BookingDto updateBooking(@AuditEntityId Long id, UpdateBookingRequest request) { ... }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {

    /** Тип сущности: BOOKING, VEHICLE, PAYMENT и т.д. */
    String entity();

    /** Действие */
    AuditAction action();

    /**
     * Индекс аргумента метода, содержащего entity ID (0-based).
     * Если = -1, аспект попытается извлечь ID из результата метода (поле id).
     */
    int entityIdParam() default -1;
}

