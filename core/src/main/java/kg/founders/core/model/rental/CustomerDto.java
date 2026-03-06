package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CustomerDto {
    private final Long id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String additionalInfo;
}
