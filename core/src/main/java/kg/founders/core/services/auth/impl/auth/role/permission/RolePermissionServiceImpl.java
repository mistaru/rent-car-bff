package kg.founders.core.services.auth.impl.auth.role.permission;

import kg.founders.core.data_access_layer.repo.permission.RolePermissionRepo;
import kg.founders.core.entity.auth.permission.RolePermission;
import kg.founders.core.services.auth.role.permission.RolePermissionService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RolePermissionServiceImpl implements RolePermissionService {

    RolePermissionRepo repo;

    @Override
    public void saveAll(List<RolePermission> rolePermissions) {
        repo.saveAll(rolePermissions);
    }

    @Override
    public void deleteAll(List<RolePermission> rolePermissions) {
        repo.deleteAll(rolePermissions);
    }
}