package dev.baskakov.eventmanagerservice.location.utils;

import dev.baskakov.eventmanagerservice.location.model.domain.Location;
import dev.baskakov.eventmanagerservice.location.model.entity.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationEntityConverter {
    public Location toDomain(LocationEntity locationEntity){
        return new Location(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription()
        );
    }

    public LocationEntity toEntity(Location location){
        return new LocationEntity(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }
}
