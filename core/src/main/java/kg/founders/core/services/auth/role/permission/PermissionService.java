package kg.founders.core.services.auth.role.permission;

import kg.founders.core.model.auth.role.permission.PermissionModel;

import java.util.List;

public interface PermissionService {
    List<PermissionModel> listAllAsModel();
}
