package kg.founders.core.converter.rental;

import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.Customer;
import kg.founders.core.model.rental.CustomerData;
import kg.founders.core.model.rental.CustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class CustomerConverter extends ModelConverter<CustomerData, Customer> {
    @PostConstruct
    public void init() {
        this.fromEntity = this::toCustomerDto;
        this.fromModel = this::toCustomerEntity;
    }

    private CustomerDto toCustomerDto(Customer customer) {
        if (customer == null) return null;
        return CustomerDto.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .additionalInfo(customer.getAdditionalInfo())
                .build();
    }

    private Customer toCustomerEntity(CustomerData request) {
        return Customer.builder()
                .fullName(request.getFullName())
                .email(blankToNull(request.getEmail()))
                .phone(blankToNull(request.getPhone()))
                .additionalInfo(request.getAdditionalInfo())
                .build();
    }

    // Пустая строка в уникальной колонке email ломала бы уникальность, поэтому храним null
    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
