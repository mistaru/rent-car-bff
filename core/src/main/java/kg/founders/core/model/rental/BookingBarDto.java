package kg.founders.core.model.rental;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingBarDto {
    private Long        id;
    private String      customerName;
    private String      customerEmail;
    private String      pickupDate;   // yyyy-MM-dd
    private String      dropoffDate;  // yyyy-MM-dd
    private BigDecimal  totalAmount;
    private List<String> addOns;
    private String      status;
    private String      paymentStatus;
}
