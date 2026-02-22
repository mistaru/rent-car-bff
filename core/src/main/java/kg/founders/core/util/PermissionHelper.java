package kg.founders.core.util;

import kg.founders.core.entity.auth.Auth;
import kg.founders.core.exceptions.ForbiddenException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionHelper {

    public Auth getCurrentPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Auth) {
            return (Auth) principal;
        } else {
            throw new ForbiddenException();
        }
    }

    public boolean isAdmin() {
        return getCurrentPrincipal().getAuthRoles().stream()
                .anyMatch(role -> "admin".equalsIgnoreCase(role.getRole().getName()));
    }

    public Long currentUserId() {
        return getCurrentPrincipal().getId();
    }
}
