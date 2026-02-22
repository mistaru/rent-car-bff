package kg.founders.core.repo;

import kg.founders.core.entity.OldPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OldPasswordRepo extends JpaRepository<OldPassword, Long> {
    List<OldPassword> findTop5ByAuthIdOrderByIdDesc(Long authId);
}