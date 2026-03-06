package kg.founders.core.repo;

import kg.founders.core.entity.rental.VehicleBlockedPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleBlockedPeriodRepository extends JpaRepository<VehicleBlockedPeriod, Long> {

    @Query("SELECT bp FROM VehicleBlockedPeriod bp " +
            "JOIN FETCH bp.vehicle " +
            "WHERE bp.vehicle.id = :vehicleId " +
            "ORDER BY bp.startDate ASC")
    List<VehicleBlockedPeriod> findByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT CASE WHEN COUNT(bp) > 0 THEN true ELSE false END " +
            "FROM VehicleBlockedPeriod bp " +
            "WHERE bp.vehicle.id = :vehicleId " +
            "AND bp.startDate < :endDate " +
            "AND bp.endDate > :startDate")
    boolean existsOverlappingBlock(
            @Param("vehicleId") Long vehicleId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /** Исключая конкретный период (для валидации при обновлении) */
    @Query("SELECT CASE WHEN COUNT(bp) > 0 THEN true ELSE false END " +
            "FROM VehicleBlockedPeriod bp " +
            "WHERE bp.vehicle.id = :vehicleId " +
            "AND bp.id <> :excludeId " +
            "AND bp.startDate < :endDate " +
            "AND bp.endDate > :startDate")
    boolean existsOverlappingBlockExcluding(
            @Param("vehicleId") Long vehicleId,
            @Param("excludeId") Long excludeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT bp FROM VehicleBlockedPeriod bp " +
            "JOIN FETCH bp.vehicle " +
            "ORDER BY bp.startDate ASC")
    List<VehicleBlockedPeriod> findAllWithVehicle();
}

