package kg.founders.core.repo;

import kg.founders.core.entity.rental.VehiclePhotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiclePhotosRepository extends JpaRepository<VehiclePhotos, Long> {
    List<VehiclePhotos> findAllByVehicleIdOrderBySortOrderAsc(Long carId);
}