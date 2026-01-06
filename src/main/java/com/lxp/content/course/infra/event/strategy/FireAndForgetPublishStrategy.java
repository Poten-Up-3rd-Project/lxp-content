package com.lxp.content.course.infra.event.strategy;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.event.EventPublishStrategy;
import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.domain.event.OutboxRequired;
import com.lxp.content.course.infra.messaging.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FireAndForgetPublishStrategy implements EventPublishStrategy {
    private final EventProducer producer;

    @Override
    public boolean supports(CrudEvent event) {
        return !event.getClass().isAnnotationPresent(OutboxRequired.class);
    }

    @Override
    public void prepare(IntegrationEvent integrationEvent, CrudEvent event) {

    }

    @Override
    public void publish(IntegrationEvent integrationEvent, CrudEvent event) {
        try {
            producer.send(integrationEvent);
        } catch (Exception e) {
            log.warn("Fire and forget failed: {}", integrationEvent.getEventId(), e);
        }
    }
}
