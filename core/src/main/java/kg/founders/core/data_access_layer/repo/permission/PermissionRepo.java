package kg.founders.core.data_access_layer.repo.permission;

import kg.founders.core.entity.auth.permission.Permission;
import kg.founders.core.model.auth.role.permission.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepo extends JpaRepository<Permission, Long> {

    @Query("select new kg.founders.core.model.auth.role.permission.PermissionModel(p.id, p.name, 0) from Permission p")
    List<PermissionModel> findAllPermissions();
}