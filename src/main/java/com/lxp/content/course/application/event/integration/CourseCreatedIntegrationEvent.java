package com.lxp.content.course.application.event.integration;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lxp.common.application.event.BaseIntegrationEventEnvelope;
import com.lxp.content.course.application.event.integration.payload.CourseCreatedPayload;

import java.time.LocalDateTime;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCreatedIntegrationEvent extends BaseIntegrationEventEnvelope<CourseCreatedPayload> {
    private static final String SOURCE = "lxp.course.service";

    @JsonCreator
    public CourseCreatedIntegrationEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("occurredAt") LocalDateTime occurredAt,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("causationId") String causationId,
            @JsonProperty("payload") CourseCreatedPayload payload
    ) {
        super(eventId, occurredAt, SOURCE, correlationId, causationId, payload);
    }


    @Override
    public String getEventType() {
        return "course.created";
    }
}