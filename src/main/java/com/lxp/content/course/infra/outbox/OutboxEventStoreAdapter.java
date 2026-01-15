package com.lxp.content.course.infra.outbox;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.content.course.application.event.integration.EventMetadata;
import com.lxp.content.course.application.port.required.OutboxEventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventStoreAdapter implements OutboxEventStore {

    private final CourseOutboxRepository outboxRepository;
    private final OutboxEventSerializer serializer;

    @Override
    public void save(IntegrationEvent event, EventMetadata metadata) {
        OutboxEvent outbox = new OutboxEvent(
                event.getEventId(),
                event.getEventType(),
                metadata.aggregateEventType(),
                metadata.aggregateId(),
                serializer.serialize(event),
                event.getOccurredAt()
        );
        outboxRepository.save(outbox);

    }
}
