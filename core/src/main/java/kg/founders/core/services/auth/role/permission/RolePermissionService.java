package kg.founders.core.services.auth.role.permission;


import kg.founders.core.entity.auth.permission.RolePermission;

import java.util.List;

public interface RolePermissionService {

    void saveAll(List<RolePermission> rolePermissions);

    void deleteAll(List<RolePermission> rolePermissions);
}
