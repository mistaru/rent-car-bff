package kg.founders.core.repo;

import kg.founders.core.entity.rental.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {
    List<VehicleImage> findByVehicleIdOrderBySortOrderAsc(Long vehicleId);
    Optional<VehicleImage> findFirstByVehicleIdAndMainTrue(Long vehicleId);
    void deleteByVehicleId(Long vehicleId);
}