package kg.founders.core.services.auth.impl.auth.role;

import kg.founders.core.data_access_layer.dao.AuthRoleDao;
import kg.founders.core.data_access_layer.repo.auth.AuthRoleRepo;
import kg.founders.core.entity.auth.Auth;
import kg.founders.core.entity.auth.role.AuthRole;
import kg.founders.core.entity.auth.role.AuthRoleId;
import kg.founders.core.entity.auth.role.Role;
import kg.founders.core.services.auth.role.AuthRoleService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthRoleServiceImpl implements AuthRoleService {

    AuthRoleDao dao;
    AuthRoleRepo repo;

    @Override
    public void updateActive(Long authId, Long roleId) {
        dao.updateActiveRole(authId, roleId);
    }

    @Override
    public List<AuthRole> saveAllByAuthId(Auth auth, List<Role> roles) {
        deleteAllByAuthId(auth.getId());
        return repo.saveAll(roles.stream().map(role -> new AuthRole(new AuthRoleId(auth.getId(), role.getId()), auth, role, false)).collect(Collectors.toList()));
    }

    @Override
    public void deleteAllByAuthId(Long authId) {
        repo.deleteAllByAuthId(authId);
    }

}