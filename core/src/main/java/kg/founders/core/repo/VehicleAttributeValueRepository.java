package kg.founders.core.repo;

import kg.founders.core.entity.rental.VehicleAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleAttributeValueRepository extends JpaRepository<VehicleAttributeValue, Long> {

    @Query("SELECT v FROM VehicleAttributeValue v JOIN FETCH v.attribute WHERE v.vehicle.id = :vehicleId")
    List<VehicleAttributeValue> findByVehicleIdWithAttribute(@Param("vehicleId") Long vehicleId);

    @Query("SELECT v FROM VehicleAttributeValue v WHERE v.vehicle.id = :vehicleId AND v.attribute.id = :attributeId")
    VehicleAttributeValue findByVehicleIdAndAttributeId(@Param("vehicleId") Long vehicleId,
                                                         @Param("attributeId") Long attributeId);

    @Query("SELECT DISTINCT v.value FROM VehicleAttributeValue v WHERE v.attribute.id = :attributeId")
    List<String> findDistinctValuesByAttributeId(@Param("attributeId") Long attributeId);

    void deleteByVehicleIdAndAttributeId(Long vehicleId, Long attributeId);

    void deleteByVehicleId(Long vehicleId);
}

