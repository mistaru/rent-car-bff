package kg.founders.core.services.rental;

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
}
