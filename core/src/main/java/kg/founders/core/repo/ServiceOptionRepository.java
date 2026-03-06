package kg.founders.core.repo;

import kg.founders.core.entity.rental.ServiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOptionRepository extends JpaRepository<ServiceOption, Long> {

    Optional<ServiceOption> findByCode(String code);

    List<ServiceOption> findByActiveTrueOrderBySortOrderAscNameAsc();

    List<ServiceOption> findAllByOrderBySortOrderAscNameAsc();

    boolean existsByCode(String code);
}

