package kg.founders.core.services.auth.impl.auth.role.permission;

import kg.founders.core.entity.auth.permission.Permission;
import kg.founders.core.enums.permission.PermissionType;
import kg.founders.core.model.auth.role.permission.PermissionModel;
import kg.founders.core.data_access_layer.repo.permission.PermissionRepo;
import kg.founders.core.services.auth.role.permission.PermissionService;
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
public class PermissionServiceImpl implements PermissionService {

    PermissionRepo repo;

    @Override
    public List<PermissionModel> listAllAsModel() {
        return repo.findAllPermissions();
    }

    public void initPermissions() {

        var permissions = listAllAsModel().stream().map(p -> new Permission(
                p.getId(),
                p.getName(),
                p.getName().getDescription())
        ).collect(Collectors.toList());

        var permissionSet = permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        for (var p : PermissionType.values()) {
            if (!permissionSet.contains(p)) {
                permissions.add(new Permission(
                        null,
                        p,
                        p.getDescription()));
            }
        }

        if (repo.saveAll(permissions).size() != permissions.size()) {
            throw new RuntimeException();
        }
    }
}
