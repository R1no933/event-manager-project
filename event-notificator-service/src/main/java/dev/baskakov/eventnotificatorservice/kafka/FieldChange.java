package dev.baskakov.eventnotificatorservice.kafka;

public record FieldChange<T>(
        T oldField,
        T newField
) {
}
