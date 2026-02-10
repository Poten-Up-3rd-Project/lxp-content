package com.lxp.content.course.qna.application.event.integration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lxp.common.application.event.BaseIntegrationEventEnvelope;
import com.lxp.content.course.qna.application.event.integration.payload.QnaCreatedPayload;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QnaCreatedIntegrationEvent extends BaseIntegrationEventEnvelope<QnaCreatedPayload> {
    private static final String SOURCE = "lxp.course.qna.service";

    @JsonCreator
    public QnaCreatedIntegrationEvent(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("occurredAt") LocalDateTime occurredAt,
        @JsonProperty("correlationId") String correlationId,
        @JsonProperty("causationId") String causationId,
        @JsonProperty("payload") QnaCreatedPayload payload
    ) {
        super(eventId, occurredAt, SOURCE, correlationId, causationId, payload);
    }

    @Override
    public String getEventType() {
        return "qna.created";
    }
}
