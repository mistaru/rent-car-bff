package kg.founders.core.services.auth.role;

import kg.founders.core.entity.auth.role.Role;
import kg.founders.core.model.auth.role.RoleModel;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleModel> listAllAsModel();

    Optional<Role> findById(Long id);

    void save(RoleModel model);

    void delete(Long roleId);

}
