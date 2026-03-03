package kg.founders.core.model.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LocationDto {
    private final Long id;
    private final String name;
    private final String city;
    private final String country;
}
