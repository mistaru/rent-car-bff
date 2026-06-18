package kg.founders.core.repo;

import kg.founders.core.entity.rental.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id")
    Optional<Vehicle> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT DISTINCT v FROM Vehicle v LEFT JOIN FETCH v.images ORDER BY v.id DESC")
    List<Vehicle> findAllWithImages();

    @Query("SELECT DISTINCT v FROM Vehicle v LEFT JOIN FETCH v.images WHERE v.id = :id")
    Optional<Vehicle> findByIdWithImages(@Param("id") Long id);
}
