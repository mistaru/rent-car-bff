package kg.founders.core.services.impl.auth.role;

import kg.founders.core.data_access_layer.repo.role.RoleRepo;
import kg.founders.core.entity.auth.permission.Permission;
import kg.founders.core.entity.auth.permission.RolePermission;
import kg.founders.core.entity.auth.role.Role;
import kg.founders.core.exceptions.ValidationException;
import kg.founders.core.model.auth.role.RoleModel;
import kg.founders.core.services.auth.role.RoleService;
import kg.founders.core.services.auth.role.permission.RolePermissionService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepo repo;
    RolePermissionService rolePermissionService;

    @Override
    public List<RoleModel> listAllAsModel() {
        return repo.findByRdtIsNull().stream().map(Role::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findById(Long roleId) {
        return repo.findById(roleId);
    }

    @Override
    @Transactional
    public void save(RoleModel model) {
        model.validate();

        Role role;

        if (model.getId() == null) {
            role = new Role();
        } else {
            role = repo.getByIdAndRdtIsNull(model.getId())
                    .orElseThrow(() -> new ValidationException(
                            "Запись с id = " + model.getId() + " не найдена"));

            rolePermissionService.deleteAll(role.getRolePermissions());
            role.setRolePermissions(null);
        }

        role.setName(model.getName());
        role.setDescription(model.getDescription());

        repo.save(role);

        rolePermissionService.saveAll(
                model.getPermissions().stream()
                        .map(pm -> new RolePermission(
                                role,
                                new Permission(pm.getId()),
                                pm.getAccess()
                        ))
                        .collect(Collectors.toList()));
    }

    @Override
    public void delete(Long roleId) {
        repo.getByIdAndRdtIsNull(roleId)
                .ifPresent(role -> {
                    role.markDeleted();
                    role.setRolePermissions(null);
                    repo.save(role);
                });
    }
}
