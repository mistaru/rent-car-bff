package kg.founders.bff.config.audit;

import com.google.gson.Gson;
import kg.founders.bff.config.settings.TokenContextHolder;
import kg.founders.core.settings.audit.AuditEntityId;
import kg.founders.core.settings.audit.Auditable;
import kg.founders.core.services.audit.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * AOP-аспект для автоматического аудит-логирования.
 * <p>
 * Перехватывает любые методы, помеченные {@link Auditable},
 * и записывает действие в таблицу {@code audit_logs}.
 * <p>
 * Логика определения entity ID (приоритет):
 * <ol>
 *   <li>Параметр, помеченный {@link AuditEntityId}</li>
 *   <li>Явно указанный индекс {@code entityIdParam} в аннотации</li>
 *   <li>Из результата метода — поле {@code id} (через reflection)</li>
 * </ol>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final Gson gson;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        // Выполняем метод
        Object result = joinPoint.proceed();

        // Логируем асинхронно-безопасно, но в той же транзакции
        try {
            Long managerId = resolveManagerId();
            String managerLogin = resolveManagerLogin();
            Long entityId = resolveEntityId(joinPoint, auditable, result);
            String body = serializeBody(joinPoint, result);

            auditLogService.log(
                    managerId,
                    managerLogin,
                    auditable.entity(),
                    entityId,
                    auditable.action().name(),
                    body
            );
        } catch (Exception e) {
            // Аудит НЕ должен ломать бизнес-логику
            log.warn("Audit logging failed for {}.{}: {}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    e.getMessage());
        }

        return result;
    }

    /**
     * Получает ID текущего менеджера из SecurityContext.
     * Для публичных эндпоинтов (без авторизации) возвращает 0.
     */
    private Long resolveManagerId() {
        return TokenContextHolder.currentOptional()
                .map(TokenContextHolder::getPrincipal)
                .filter(java.util.Objects::nonNull)
                .map(auth -> auth.getId())
                .orElse(0L);
    }

    private String resolveManagerLogin() {
        return TokenContextHolder.currentOptional()
                .map(TokenContextHolder::getPrincipal)
                .filter(java.util.Objects::nonNull)
                .map(auth -> auth.getUsername())
                .orElse("system");
    }

    /**
     * Извлекает entity ID из:
     * 1) параметра с @AuditEntityId
     * 2) явного entityIdParam
     * 3) поля id результата
     */
    private Long resolveEntityId(ProceedingJoinPoint joinPoint, Auditable auditable, Object result) {
        Object[] args = joinPoint.getArgs();
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        Method method = sig.getMethod();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        // 1. Ищем параметр с @AuditEntityId
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation ann : paramAnnotations[i]) {
                if (ann instanceof AuditEntityId && args[i] != null) {
                    return toLong(args[i]);
                }
            }
        }

        // 2. Явный индекс
        if (auditable.entityIdParam() >= 0 && auditable.entityIdParam() < args.length) {
            Object arg = args[auditable.entityIdParam()];
            if (arg != null) return toLong(arg);
        }

        // 3. Из результата — поле id
        Object body = unwrapResponseEntity(result);
        if (body != null) {
            try {
                var field = body.getClass().getDeclaredField("id");
                field.setAccessible(true);
                Object id = field.get(body);
                if (id != null) return toLong(id);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                // попробуем getId()
                try {
                    var getter = body.getClass().getMethod("getId");
                    Object id = getter.invoke(body);
                    if (id != null) return toLong(id);
                } catch (Exception ignored2) {
                    // ok
                }
            }
        }

        return 0L;
    }

    /**
     * Сериализует тело запроса (аргументы кроме простых id) для diff_json.
     */
    private String serializeBody(ProceedingJoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            // Ищем первый сложный объект (request body)
            for (Object arg : args) {
                if (arg != null && !isPrimitive(arg)) {
                    return gson.toJson(arg);
                }
            }
            // Если нет request body, логируем результат
            Object body = unwrapResponseEntity(result);
            if (body != null) {
                return gson.toJson(body);
            }
        } catch (Exception e) {
            log.debug("Failed to serialize audit body: {}", e.getMessage());
        }
        return null;
    }

    private Object unwrapResponseEntity(Object result) {
        if (result instanceof ResponseEntity) {
            return ((ResponseEntity<?>) result).getBody();
        }
        return result;
    }

    private boolean isPrimitive(Object obj) {
        return obj instanceof Number || obj instanceof String
                || obj instanceof Boolean || obj instanceof Character;
    }

    private Long toLong(Object val) {
        if (val instanceof Long) return (Long) val;
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            return Long.parseLong(val.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}

