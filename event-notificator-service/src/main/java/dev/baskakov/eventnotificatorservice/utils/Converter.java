package dev.baskakov.eventnotificatorservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.baskakov.eventnotificatorservice.model.domain.EventNotificationMessage;
import dev.baskakov.eventnotificatorservice.model.domain.FieldChange;
import dev.baskakov.eventnotificatorservice.model.dto.NotificationResponseDTO;
import dev.baskakov.eventnotificatorservice.model.entity.NotificationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Converter {

    private static final Logger log = LoggerFactory.getLogger(Converter.class);
    private final ObjectMapper objectMapper;

    public Converter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NotificationResponseDTO toResponseDTO(
            NotificationEntity entity
    ) {
        Map<String,Object> fieldChanges = parseFieldChanges(entity.getFieldChanges());
        return new NotificationResponseDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getEventId(),
                entity.getChangedByUserId(),
                entity.getNotificationType(),
                fieldChanges,
                entity.getRead(),
                entity.getCreatedAt(),
                entity.getReadAt()
        );
    }

    public String convertFieldChangesToJson(EventNotificationMessage message) {
        Map<String, FieldChange<?>> fieldChanges = new HashMap<>();

        if(message.name() != null) fieldChanges.put("name", message.name());
        if(message.maxPlaces() != null) fieldChanges.put("maxPlaces", message.maxPlaces());
        if(message.date() != null) fieldChanges.put("date", message.date());
        if(message.cost() != null) fieldChanges.put("cost", message.cost());
        if(message.duration() != null) fieldChanges.put("duration", message.duration());
        if(message.locationId() != null) fieldChanges.put("locationId", message.locationId());
        if(message.status() != null) fieldChanges.put("status", message.status());

        try {
            return objectMapper.writeValueAsString(fieldChanges);
        } catch (JsonProcessingException e) {
            log.error("Could not convert field changes to json", e);
            return "{}";
        }

    }

    private Map<String, Object> parseFieldChanges(
            String fieldsChangesJson
    ) {
        if (fieldsChangesJson == null || fieldsChangesJson.trim().isEmpty()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(fieldsChangesJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException ex) {
            log.error("Could not parse field changes {}", fieldsChangesJson, ex);
            return Map.of("error", "Could not parse field changes ");
        }
    }
}
