package kg.founders.core.model.rental;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Запрос на обновление бронирования из админки.
 * Можно менять: даты, статус, локации, клиентские данные.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookingRequest {

    private LocalDate pickupDate;
    private LocalDate dropoffDate;
    private Long pickupLocationId;
    private Long dropoffLocationId;
    private String status; // DRAFT, PENDING_PAYMENT, CONFIRMED, CANCELLED

    // Клиентские данные
    private String customerFullName;
    private String customerEmail;
    private String customerPhone;
    private String customerAdditionalInfo;

    /** Комментарий менеджера (записывается в историю изменений) */
    private String managerComment;

    /** Обновлённые доп. услуги (null = не менять, пустой список = удалить все) */
    private List<AddOnRequest> addOns;
}

