package kg.founders.core.services.rental.impl;

import kg.founders.core.converter.rental.CustomerConverter;
import kg.founders.core.entity.rental.Customer;
import kg.founders.core.exceptions.NotFoundException;
import kg.founders.core.model.rental.CreateCustomerRequest;
import kg.founders.core.model.rental.CustomerDto;
import kg.founders.core.repo.CustomerRepository;
import kg.founders.core.services.rental.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerConverter customerConverter;

    @Transactional
    @Override
    public CustomerDto createCustomer(CreateCustomerRequest request) {
        // Если клиент с таким email уже существует — вернуть его
        var existing = customerRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            log.info("Customer with email {} already exists, returning existing id: {}",
                    request.getEmail(), existing.get().getId());
            return (CustomerDto) customerConverter.convertFromEntity(existing.get());
        }
        Customer customer = customerConverter.convertFromModel(request);
        customer = customerRepository.save(customer);
        log.info("Created customer with id: {}", customer.getId());
        return (CustomerDto) customerConverter.convertFromEntity(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        return (CustomerDto) customerConverter.convertFromEntity(customer);
    }
}
