package kg.founders.core.entity.auth.role;

import kg.founders.core.entity.auth.Auth;
import kg.founders.core.util.SqlTable;
import kg.founders.core.util.GsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = AuthRole.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRole implements Serializable {

    @SqlTable
    public static final String TABLE_NAME = "AUTH_ROLES";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @EmbeddedId
    AuthRoleId id;

    @ManyToOne
    @GsonIgnore
    @MapsId("authId")
    Auth auth;

    @ManyToOne
    @GsonIgnore
    @MapsId("roleId")
    Role role;

    @Column(name = "ACTIVE")
    Boolean active;

    public static AuthRole createForUpdate(Auth auth, Role role, Boolean active) {
        var ar = new AuthRole();
        ar.id = new AuthRoleId(auth.getId(), role.getId());
        ar.auth = auth;
        ar.role = role;
        ar.active = active;
        return ar;
    }

    public static AuthRole createForInsert(Auth auth, Role role, Boolean active) {
        var ar = new AuthRole();
        ar.id = new AuthRoleId();
        ar.auth = auth;
        ar.role = role;
        ar.active = active;
        return ar;
    }
}