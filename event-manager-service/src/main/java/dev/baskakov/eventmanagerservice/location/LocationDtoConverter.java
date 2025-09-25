package dev.baskakov.eventmanagerservice.location;

import org.springframework.stereotype.Component;

@Component
public class LocationDtoConverter {
    public Location toDomain(LocationDTO locationDTO) {
        return new Location(
                locationDTO.id(),
                locationDTO.name(),
                locationDTO.address(),
                locationDTO.capacity(),
                locationDTO.description()
        );
    }

    public LocationDTO toDTO(Location location) {
        return new LocationDTO(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }
}
