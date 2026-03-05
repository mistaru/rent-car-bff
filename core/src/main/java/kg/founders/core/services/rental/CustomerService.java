package kg.founders.core.services.rental;

import kg.founders.core.converter.RentalMapper;
import kg.founders.core.entity.rental.Customer;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.CreateCustomerRequest;
import kg.founders.core.model.rental.CustomerDto;
import kg.founders.core.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RentalMapper rentalMapper;

    @Transactional
    public CustomerDto createCustomer(CreateCustomerRequest request) {
        // Если клиент с таким email уже существует — вернуть его
        var existing = customerRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            log.info("Customer with email {} already exists, returning existing id: {}",
                    request.getEmail(), existing.get().getId());
            return rentalMapper.toCustomerDto(existing.get());
        }
        Customer customer = rentalMapper.toCustomerEntity(request);
        customer = customerRepository.save(customer);
        log.info("Created customer with id: {}", customer.getId());
        return rentalMapper.toCustomerDto(customer);
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        return rentalMapper.toCustomerDto(customer);
    }
}
