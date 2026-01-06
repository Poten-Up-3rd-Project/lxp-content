package com.lxp.content.course.infra.event;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.application.port.out.IntegrationEventPublisher;
import com.lxp.content.course.infra.messaging.EventProducer;
import com.lxp.content.course.infra.messaging.exception.EventPublishException;
import com.lxp.content.course.infra.messaging.exception.EventSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class CourseIntegrationEventPublisher implements IntegrationEventPublisher {

    private final EventProducer producer;

    @Override
    public void publish(IntegrationEvent event) {
        try {
            producer.send(event);
        } catch (EventSerializationException e) {
            log.error("Serialization failed - bug?: {}", event.getEventId(), e);
        } catch (EventPublishException e) {
            // 재시도 가능, 추후 Outbox에 저장
            log.warn("Publish failed, will retry: {}", event.getEventId(), e);
            throw e;
        }
    }

    @Override
    public void publish(String topic, IntegrationEvent event) {
        return;
    }

}
