package kg.founders.core.repo;

import kg.founders.core.entity.rental.VehicleAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleAttributeRepository extends JpaRepository<VehicleAttribute, Long> {

    Optional<VehicleAttribute> findByCode(String code);

    boolean existsByCode(String code);

    List<VehicleAttribute> findByActiveTrueOrderBySortOrderAscNameAsc();

    List<VehicleAttribute> findByActiveTrueAndFilterableTrueOrderBySortOrderAsc();

    List<VehicleAttribute> findAllByOrderBySortOrderAscNameAsc();
}

