package dev.baskakov.eventmanagerservice.location.controller;

import dev.baskakov.eventmanagerservice.location.model.domain.Location;
import dev.baskakov.eventmanagerservice.location.model.dto.LocationDTO;
import dev.baskakov.eventmanagerservice.location.service.LocationService;
import dev.baskakov.eventmanagerservice.location.utils.LocationDtoConverter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);

    private final LocationService locationService;
    private final LocationDtoConverter locationDtoConverter;

    public LocationController(LocationService locationService,
                              LocationDtoConverter locationDtoConverter
    ) {
        this.locationService = locationService;
        this.locationDtoConverter = locationDtoConverter;
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(
            @RequestBody @Valid LocationDTO locationToCreate
    ) {
        LOGGER.info("Get request to create location : {}", locationToCreate);
        Location requestLocation = locationService.createLocation(
                locationDtoConverter.toDomain(locationToCreate));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationDtoConverter.toDTO(requestLocation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(
            @PathVariable("id") Long locationId
    ) {
        LOGGER.info("Get request to get location with id: {}", locationId);
        Location foundLocation = locationService.getLocationById(locationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationDtoConverter.toDTO(foundLocation));
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        LOGGER.info("Get request to get all locations");
        var locations = locationService.getAllLocation();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locations
                        .stream()
                        .map(locationDtoConverter::toDTO)
                        .toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationById(
            @PathVariable("id") Long locationId
    ) {
        LOGGER.info("Delete request to delete location with id: {}", locationId);
        locationService.deleteLocationById(locationId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocationById(
            @PathVariable("id") Long locationId,
            @RequestBody @Valid LocationDTO locationToUpdate
    ) {
        LOGGER.info("Update request to update location with id: {}", locationId);
        var updatedLocation = locationService.updateLocationById(
                locationId ,
                locationDtoConverter.toDomain(locationToUpdate));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationDtoConverter.toDTO(updatedLocation));
    }
}
