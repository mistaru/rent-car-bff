package kg.founders.core.data_access_layer.repo.auth;

import kg.founders.core.entity.auth.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthRepo extends JpaRepository<Auth, Long> {
    Optional<Auth> findByUsername(String username);

    List<Auth> findAllByRdtIsNull();

}