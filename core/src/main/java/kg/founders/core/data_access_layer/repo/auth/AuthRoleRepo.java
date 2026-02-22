package kg.founders.core.data_access_layer.repo.auth;

import kg.founders.core.entity.auth.role.AuthRole;
import kg.founders.core.entity.auth.role.AuthRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRoleRepo extends JpaRepository<AuthRole, AuthRoleId> {

    void deleteAllByAuthId(Long authId);
}