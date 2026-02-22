package kg.founders.core.data_access_layer.repo.role;

import kg.founders.core.entity.auth.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {

    Optional<Role> getByIdAndRdtIsNull(Long id);

    List<Role> findByRdtIsNull();
}
