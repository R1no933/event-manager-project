package dev.baskakov.eventmanagerservice.location;

import dev.baskakov.eventmanagerservice.location.utils.LocationEntityConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationEntityConverter locationEntityConverter;

    public LocationService(LocationRepository locationRepository,
                           LocationEntityConverter locationEntityConverter) {
        this.locationRepository = locationRepository;
        this.locationEntityConverter = locationEntityConverter;
    }

    public Location createLocation(Location location) {
        var locationToSave = locationEntityConverter.toEntity(location);
        var savedLocation = locationRepository.save(locationToSave);
        return locationEntityConverter.toDomain(savedLocation);
    }

    public Location getLocationById(Long locationId) {
        var  locationToFind = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + locationId));
        return locationEntityConverter.toDomain(locationToFind);
    }

    public List<Location> getAllLocation() {
        return locationRepository
                .findAll()
                .stream()
                .map(locationEntityConverter::toDomain)
                .toList();
    }

    public void deleteLocationById(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Location not found with id: " + locationId);
        }
        locationRepository.deleteById(locationId);
    }

    public Location updateLocationById(Long locationId, Location updatedLocation) {
        if (!locationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Location not found with id: " + locationId);
        }

        locationRepository.updateLocation(
                locationId,
                updatedLocation.name(),
                updatedLocation.address(),
                updatedLocation.capacity(),
                updatedLocation.description()
        );

        return locationEntityConverter
                .toDomain(locationRepository
                        .findById(locationId)
                        .orElseThrow());
    }
}
