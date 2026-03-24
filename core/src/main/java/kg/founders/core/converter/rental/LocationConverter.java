package kg.founders.core.converter.rental;

import kg.founders.core.converter.ModelConverter;
import kg.founders.core.entity.rental.Location;
import kg.founders.core.model.rental.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class LocationConverter extends ModelConverter<LocationDto, Location> {
    @PostConstruct
    public void init() {
        this.fromEntity = this::toLocationDto;
    }

    private LocationDto toLocationDto(Location location) {
        if (location == null) return null;
        return LocationDto.builder()
                .id(location.getId())
                .name(location.getName())
                .city(location.getCity())
                .country(location.getCountry())
                .build();
    }
}
