package kg.founders.core.entity.auth.role;

import kg.founders.core.entity.BaseEntity;
import kg.founders.core.entity.auth.permission.RolePermission;
import kg.founders.core.model.audit.IdBased;
import kg.founders.core.util.SqlTable;
import kg.founders.core.model.auth.role.RoleModel;
import kg.founders.core.model.auth.role.permission.PermissionModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Role.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Role extends BaseEntity implements IdBased {

    @SqlTable
    public static final String TABLE_NAME = "ROLES";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    @GeneratedValue(generator = SEQ_NAME)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(length = 1000)
    String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
    List<RolePermission> rolePermissions;

    public Role(Long id) {
        this.id = id;
    }

    public RoleModel toModel() {
        List<PermissionModel> permissionModels = Collections.emptyList();

        var permissions = getRolePermissions();

        if (permissions != null) {
            permissionModels = permissions.stream()
                    .map(p -> p.getPermission().toModel(p))
                    .collect(Collectors.toList());
        }

        return RoleModel.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .permissions(permissionModels)
                .build();
    }
}
