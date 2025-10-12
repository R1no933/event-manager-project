package dev.baskakov.eventmanagerservice.kafka;

public record FieldChange<T>(
        T oldField,
        T newField
) {
}
