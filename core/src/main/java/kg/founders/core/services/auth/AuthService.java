package kg.founders.core.services.auth;

import kg.founders.core.entity.auth.Auth;
import kg.founders.core.model.audit.AuditModel;
import kg.founders.core.model.auth.AuthModel;
import kg.founders.core.model.login.PasswordChangeModel;

import java.util.List;
import java.util.Optional;

public interface AuthService {

    Optional<Auth> findByUsername(String username);

    Optional<Auth> findById(Long authId);

    boolean blockAuth(Long authId, boolean block);

    boolean blockAuth(String username, boolean block);

    void updatePassword(Auth bankAuth, PasswordChangeModel model, AuditModel auditModel);

    Auth save(AuthModel bankAuth);

    Auth save(Auth bankAuth);

    Boolean isBlocked(Long id);

    String hashPassword(String password);

    AuthModel toModel(Auth auth);

    void updateActiveRole(Long id, Long roleId);

    List<AuthModel> findAll();

    void deleteByAuthId(Long authId);
}
