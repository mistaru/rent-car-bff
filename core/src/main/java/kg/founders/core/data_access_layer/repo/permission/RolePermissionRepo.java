package kg.founders.core.data_access_layer.repo.permission;

import kg.founders.core.entity.auth.permission.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepo extends JpaRepository<RolePermission, Long> {
}
