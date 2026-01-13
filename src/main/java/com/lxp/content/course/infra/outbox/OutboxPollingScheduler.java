package com.lxp.content.course.infra.outbox;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.content.course.application.port.required.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPollingScheduler {

    private final CourseOutboxRepository outboxRepository;
    private final OutboxEventSerializer serializer;
    private final EventProducer eventProducer;

    private static final int BATCH_SIZE = 100;
    private static final int MAX_RETRY_COUNT = 3;

    @Scheduled(fixedDelayString = "${outbox.polling.interval:5000}")
    @Transactional
    public void pollAndPublish() {
        List<OutboxEvent> pendingEvents = outboxRepository
                .findByStatusAndRetryCountLessThanOrderByOccurredAtAsc(
                        OutboxEvent.OutboxStatus.PENDING,
                        MAX_RETRY_COUNT,
                        BATCH_SIZE
                );

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Polling {} pending outbox events", pendingEvents.size());

        for (OutboxEvent outbox : pendingEvents) {
            publishEvent(outbox);
        }
    }

    @Scheduled(fixedDelayString = "${outbox.retry.interval:60000}")
    @Transactional
    public void retryFailedEvents() {
        List<OutboxEvent> failedEvents = outboxRepository
                .findByStatusAndRetryCountLessThanOrderByOccurredAtAsc(
                        OutboxEvent.OutboxStatus.FAILED,
                        MAX_RETRY_COUNT,
                        BATCH_SIZE
                );

        if (failedEvents.isEmpty()) {
            return;
        }

        log.info("Retrying {} failed outbox events", failedEvents.size());

        for (OutboxEvent outbox : failedEvents) {
            publishEvent(outbox);
        }
    }

    private void publishEvent(OutboxEvent outbox) {
        try {
            IntegrationEvent event = serializer.deserialize(outbox);
            eventProducer.send(event);
            outbox.markAsPublished();
            log.debug("Published outbox event: {}", outbox.getEventId());
        } catch (Exception e) {
            outbox.markAsFailed(e.getMessage());
            log.warn("Failed to publish outbox event: eventId={}, retryCount={}, error={}",
                    outbox.getEventId(), outbox.getRetryCount(), e.getMessage());
        }
        outboxRepository.save(outbox);
    }
}