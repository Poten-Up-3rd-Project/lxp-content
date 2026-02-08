package com.lxp.content.course.qna.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.content.course.infra.outbox.OutboxEventSerializer;
import com.lxp.content.course.qna.application.event.integration.QnaCreatedIntegrationEvent;
import com.lxp.content.course.qna.application.event.integration.payload.QnaCreatedPayload;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class OutboxEventSerializerTest {

    @Test
    void serialize_and_deserialize_qna_created() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OutboxEventSerializer ser = new OutboxEventSerializer(mapper);
        QnaCreatedIntegrationEvent event = new QnaCreatedIntegrationEvent(
            "e1",
            LocalDateTime.now(),
            "e1",
            null,
            new QnaCreatedPayload(
                new QnaCreatedPayload.Course("c", "CT"),
                new QnaCreatedPayload.Section("s", "ST"),
                new QnaCreatedPayload.Lecture("l", "LT"),
                new QnaCreatedPayload.Qna("q", "a", "T", "B", LocalDateTime.now())
            )
        );

        String payload = ser.serialize(event);
        OutboxEvent outbox = new OutboxEvent(
            event.getEventId(),
            event.getEventType(),
            "QnaCreatedEvent",
            "q",
            payload,
            event.getOccurredAt()
        );

        IntegrationEvent restored = ser.deserialize(outbox);
        assertThat(restored).isInstanceOf(QnaCreatedIntegrationEvent.class);
        QnaCreatedIntegrationEvent r = (QnaCreatedIntegrationEvent) restored;
        assertThat(r.getEventType()).isEqualTo("qna.created");
        assertThat(r.getPayload().course().title()).isEqualTo("CT");
    }
}
