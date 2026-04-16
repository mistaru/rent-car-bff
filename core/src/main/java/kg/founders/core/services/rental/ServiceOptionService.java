package kg.founders.core.services.rental;

import kg.founders.core.entity.rental.Booking;
import kg.founders.core.model.rental.AddOnRequest;
import kg.founders.core.model.rental.ServiceOptionDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ServiceOptionService {
    @Transactional(readOnly = true)
    List<ServiceOptionDto> getAll();

    @Transactional(readOnly = true)
    List<ServiceOptionDto> getActive();

    @Transactional(readOnly = true)
    ServiceOptionDto getById(Long id);

    @Transactional
    ServiceOptionDto create(ServiceOptionDto dto);

    @Transactional
    ServiceOptionDto update(Long id, ServiceOptionDto dto);

    @Transactional
    void delete(Long id);

    List<String> getAddOnsNamesByBooking(Booking booking);

    /**
     * Проверяет доступность инвентаря для списка доп. услуг.
     * Выбрасывает BadRequestException если какой-то услуги не хватает.
     * @param addOns запрашиваемые услуги
     * @param excludeBookingId если не null — исключить это бронирование из подсчёта занятых (для обновления)
     */
    @Transactional(readOnly = true)
    void validateInventoryAvailability(List<AddOnRequest> addOns, Long excludeBookingId);
}
