package com.lxp.content.course.infra.event.strategy;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.content.course.application.event.EventPublishStrategy;
import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.domain.event.OutboxRequired;
import com.lxp.content.course.infra.messaging.EventProducer;
import com.lxp.content.course.infra.outbox.CourseOutboxRepository;
import com.lxp.content.course.infra.outbox.OutboxEventSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPublishStrategy implements EventPublishStrategy {
    private final CourseOutboxRepository outboxRepository;
    private final EventProducer producer;
    private final OutboxEventSerializer serializer;


    @Override
    public boolean supports(CrudEvent event) {
        return event.getClass().isAnnotationPresent(OutboxRequired.class);
    }

    @Override
    public void prepare(IntegrationEvent integrationEvent, CrudEvent event) {
        OutboxEvent outbox = new OutboxEvent(
                integrationEvent.getEventId(),
                integrationEvent.getEventType(),
                event.getClass().getSimpleName(),
                event.getAggregateId(),
                serializer.serialize(integrationEvent),
                integrationEvent.getOccurredAt()
        );
        outboxRepository.save(outbox);

    }

    @Override
    public void publish(IntegrationEvent integrationEvent, CrudEvent event) {
        outboxRepository.findByEventId(event.getEventId())
                .ifPresent(this::tryPublish);
    }

    public void tryPublish(OutboxEvent outbox) {
        try {
            IntegrationEvent event = serializer.deserialize(outbox);
            producer.send(event);
            outbox.markAsPublished();
        } catch (Exception e) {
            outbox.markAsFailed(e.getMessage());
            log.warn("Outbox publish failed: {}", outbox.getEventId(), e);
        }
        outboxRepository.save(outbox);
    }
}
