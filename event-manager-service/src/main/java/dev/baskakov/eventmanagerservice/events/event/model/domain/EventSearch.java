package dev.baskakov.eventmanagerservice.events.event.model.domain;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;

import java.time.LocalDateTime;

public record EventSearch(
        String name,
        Integer placesMin,
        Integer placesMax,
        LocalDateTime dateStartAfter,
        LocalDateTime dateStartBefore,
        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Long locationId,
        EventStatus eventStatus
) {
//    public EventSearch {
//        validateFilter();
//    }
//
//    private void validateFilter() {
//        if (placesMin != null && placesMax != null && placesMin > placesMax) {
//            throw new IllegalArgumentException("placesMin cannot be greater than placesMax");
//        }
//
//        if (costMin != null && costMax != null && costMin > costMax) {
//            throw new IllegalArgumentException("costMin cannot be greater than costMax");
//        }
//
//        if (durationMin != null && durationMax != null && durationMin > durationMax) {
//            throw new IllegalArgumentException("durationMin cannot be greater than durationMax");
//        }
//
//        if (dateStartAfter != null && dateStartBefore != null && dateStartAfter.isAfter(dateStartBefore)) {
//            throw new IllegalArgumentException("dateStartAfter cannot be after dateStartBefore");
//        }
//    }
}
