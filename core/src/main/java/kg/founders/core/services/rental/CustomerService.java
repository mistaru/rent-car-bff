package kg.founders.core.services.rental;

import kg.founders.core.model.rental.CreateCustomerRequest;
import kg.founders.core.model.rental.CustomerDto;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerService {
    @Transactional
    CustomerDto createCustomer(CreateCustomerRequest request);

    @Transactional(readOnly = true)
    CustomerDto getCustomerById(Long id);
}
