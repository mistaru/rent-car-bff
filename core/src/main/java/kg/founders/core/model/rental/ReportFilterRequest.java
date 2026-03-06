package kg.founders.core.model.rental;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportFilterRequest {
    /** Начало периода (YYYY-MM-DD) */
    private String dateFrom;
    /** Конец периода (YYYY-MM-DD) */
    private String dateTo;
    /** Фильтр по статусу бронирования: CONFIRMED, CANCELLED, PENDING_PAYMENT, DRAFT */
    private String bookingStatus;
    /** Фильтр по статусу оплаты: SUCCESS, FAILED, INITIATED */
    private String paymentStatus;
    /** Фильтр по ID автомобиля */
    private Long vehicleId;
    /** Фильтр по классу авто */
    private String carClass;
}

