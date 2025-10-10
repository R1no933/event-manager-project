package dev.baskakov.eventnotificatorservice.model.domain;

public record FieldChange<T>(
        T oldField,
        T newField
) {
}
