package kg.founders.core.entity.auth;

import kg.founders.core.entity.BaseEntity;
import kg.founders.core.entity.auth.role.AuthRole;
import kg.founders.core.entity.auth.permission.GrantHolder;
import kg.founders.core.entity.auth.permission.RolePermission;
import kg.founders.core.model.audit.AuditModel;
import kg.founders.core.model.audit.CreatedByDetails;
import kg.founders.core.model.audit.IdBased;
import kg.founders.core.model.audit.ModifiedByDetails;
import kg.founders.core.util.SqlTable;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Auth.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Auth extends BaseEntity implements UserDetails, IdBased {

    @SqlTable
    public static final String TABLE_NAME = "AUTH";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    @GeneratedValue(generator = SEQ_NAME)
    Long id;

    @Column(nullable = false, unique = true)
    String username;

    String password;

    Timestamp blocked;

    @OneToMany(mappedBy = "auth", fetch = FetchType.EAGER)
    Set<AuthRole> authRoles;

    @LastModifiedDate
    Timestamp lastActivity;

    Timestamp passwordExpireDate;

    @Transient
    transient Collection<GrantHolder> authorities;

    public Auth(Long id) {
        this.id = id;
    }

    public Auth(
            Long id,
            String username,
            String password,
            Timestamp lastActivity,
            AuditModel createdBy,
            AuditModel modifiedBy,
            Timestamp cdt,
            Timestamp mdt,
            Timestamp rdt,
            Set<AuthRole> roles
    ) {
        super(createdBy, modifiedBy, cdt, mdt, rdt);
        this.id = id;
        this.username = username;
        this.password = password;
        this.lastActivity = lastActivity;
        setAuthRoles(roles);
    }

    public Auth(
            Long id,
            String username,
            String password,
            Timestamp blocked,
            Timestamp lastActivity,
            CreatedByDetails createdBy,
            ModifiedByDetails modifiedBy,
            Timestamp cdt,
            Timestamp mdt,
            Timestamp rdt,
            Set<AuthRole> roles
    ) {
        super(createdBy, modifiedBy, cdt, mdt, rdt);
        this.id = id;
        this.username = username;
        this.password = password;
        this.blocked = blocked;
        this.lastActivity = lastActivity;
        setAuthRoles(roles);
    }

    public void setAuthRoles(Set<AuthRole> authRoles) {
        this.authRoles = authRoles;

        this.authorities = authRoles == null ? Collections.emptySet() : authRoles.stream()
                .filter(AuthRole::getActive)
                .flatMap(logisticAuthRole -> logisticAuthRole.getRole().getRolePermissions().stream())
                .map(RolePermission::toGrantHolder)
                .collect(Collectors.toSet());
    }

    public void addToTheExpireDateDays(int days) {
        setPasswordExpireDate(Timestamp.valueOf(LocalDateTime.now().plusDays(days)));
    }

    @Override
    public Collection<GrantHolder> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return blocked == null;
    }
}