package dev.baskakov.eventmanagerservice.location.model.domain;

public record Location(

        Long id,

        String name,

        String address,

        Integer capacity,

        String description
) {
}
