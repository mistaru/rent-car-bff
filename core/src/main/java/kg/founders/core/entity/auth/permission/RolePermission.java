package kg.founders.core.entity.auth.permission;

import kg.founders.core.entity.auth.role.Role;
import kg.founders.core.enums.permission.AccessType;
import kg.founders.core.util.SqlTable;
import kg.founders.core.settings.security.permission.AccessPermission;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = RolePermission.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class RolePermission implements AccessPermission {

    @SqlTable
    public static final String TABLE_NAME = "ROLE_PERMISSIONS";

    @EmbeddedId
    RolePermissionId id;

    @ManyToOne
    @MapsId("roleId")
    Role role;

    @ManyToOne
    @MapsId("permissionId")
    Permission permission;

    @Column(nullable = false)
    int permissionAccess;

    public RolePermission(Role role, Permission permission, int permissionAccess) {
        this.id = new RolePermissionId();
        this.role = role;
        this.permission = permission;
        this.permissionAccess = permissionAccess;
    }

    public void setPermissionAccess(int permissionAccess) {
        this.permissionAccess = permissionAccess & AccessType.ALL.getMask();
    }

    public GrantHolder toGrantHolder() {
        return new GrantHolder(permission.getName(), getPermissionAccess());
    }

    @Override
    public int getAccess() {
        return getPermissionAccess();
    }
}
