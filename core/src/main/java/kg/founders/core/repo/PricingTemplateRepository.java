package kg.founders.core.repo;

import kg.founders.core.entity.rental.PricingTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingTemplateRepository extends JpaRepository<PricingTemplate, Long> {

    @Query("SELECT pt FROM PricingTemplate pt LEFT JOIN FETCH pt.tiers WHERE pt.id = :id")
    Optional<PricingTemplate> findByIdWithTiers(@Param("id") Long id);

    @Query("SELECT DISTINCT pt FROM PricingTemplate pt LEFT JOIN FETCH pt.tiers WHERE pt.active = true")
    List<PricingTemplate> findAllActiveWithTiers();

    @Query("SELECT DISTINCT pt FROM PricingTemplate pt LEFT JOIN FETCH pt.tiers")
    List<PricingTemplate> findAllWithTiers();

    boolean existsByNameIgnoreCase(String name);
}

