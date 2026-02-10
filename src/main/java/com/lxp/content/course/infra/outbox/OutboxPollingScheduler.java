package com.lxp.content.course.infra.outbox;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.content.course.application.port.required.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Scheduled(fixedDelayString = "${outbox.dlq.interval:30000}")
    @Transactional
    public void processDlqEvents() {
        List<OutboxEvent> dlqCandidates = outboxRepository
                .findDlqCandidates(MAX_RETRY_COUNT, BATCH_SIZE);

        if (dlqCandidates.isEmpty()) {
            return;
        }

        log.info("Processing {} DLQ candidate events", dlqCandidates.size());

        for (OutboxEvent outbox : dlqCandidates) {
            sendToDlq(outbox);
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

    private void sendToDlq(OutboxEvent outbox) {
        try {
            eventProducer.sendToDlq(outbox.getPayload(), outbox.getEventType());
            outboxRepository.markAsDlq(outbox.getId(), LocalDateTime.now());
            log.info("Sent outbox event to DLQ: eventId={}, eventType={}",
                    outbox.getEventId(), outbox.getEventType());
        } catch (Exception e) {
            log.error("Failed to send outbox event to DLQ: eventId={}, error={}",
                    outbox.getEventId(), e.getMessage());
        }
    }
}