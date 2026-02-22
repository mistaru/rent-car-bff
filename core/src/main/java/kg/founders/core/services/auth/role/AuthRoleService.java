package kg.founders.core.services.auth.role;


import kg.founders.core.entity.auth.Auth;
import kg.founders.core.entity.auth.role.AuthRole;
import kg.founders.core.entity.auth.role.Role;

import java.util.List;

public interface AuthRoleService {

    void updateActive(Long authId, Long roleId);

    List<AuthRole> saveAllByAuthId(Auth auth, List<Role> roles);

    void deleteAllByAuthId(Long authId);

}