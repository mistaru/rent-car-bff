package kg.founders.core.entity.rental;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "price_tiers", indexes = {
        @Index(name = "idx_price_tier_template", columnList = "pricing_template_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_template_id", nullable = false)
    private PricingTemplate pricingTemplate;

    @Column(nullable = false)
    private Integer minDays;

    /** null означает «без ограничения» (∞) */
    private Integer maxDays;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerDay;
}

