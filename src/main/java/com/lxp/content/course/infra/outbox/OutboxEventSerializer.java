package com.lxp.content.course.infra.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.content.course.infra.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.content.course.infra.messaging.exception.EventSerializationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventSerializer {

    private final ObjectMapper objectMapper;

    public String serialize(IntegrationEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new EventSerializationException("Serialization failed", e);
        }
    }

    public IntegrationEvent deserialize(OutboxEvent outbox) {
        try {
            Class<? extends IntegrationEvent> eventClass = resolveEventClass(outbox.getEventType());
            return objectMapper.readValue(outbox.getPayload(), eventClass);
        } catch (Exception e) {
            throw new EventSerializationException("Deserialization failed", e);
        }
    }

    private Class<? extends IntegrationEvent> resolveEventClass(String eventType) {
        return switch (eventType) {
            case "course.created" -> CourseCreatedIntegrationEvent.class;
//            case "course.updated" -> CourseUpdatedIntegrationEvent.class;
//            case "course.deleted" -> CourseDeletedIntegrationEvent.class;
            default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
        };
    }
}