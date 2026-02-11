package com.lxp.content.course.application.event.integration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lxp.common.application.event.BaseIntegrationEventEnvelope;
import com.lxp.content.course.application.event.integration.payload.CourseDeletedPayload;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDeletedIntegrationEvent  extends BaseIntegrationEventEnvelope<CourseDeletedPayload> {
    private static final String SOURCE = "lxp.course.service";

    @JsonCreator
    public CourseDeletedIntegrationEvent(
            String eventId,
            LocalDateTime occurredAt,
            String correlationId,
            String causationId,
            CourseDeletedPayload payload
    ) {
        super(eventId, occurredAt, SOURCE, correlationId, causationId, payload);
    }
    @Override
    public String getEventType() {
        return "course.deleted";
    }
}
