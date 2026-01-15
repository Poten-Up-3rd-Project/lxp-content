package com.lxp.content.course.application.event.policy;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.event.integration.EventMetadata;
import com.lxp.content.course.application.port.required.EventProducer;
import com.lxp.content.course.application.port.required.OutboxEventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultIntegrationEventRegistry implements IntegrationEventRegistry {

    private final OutboxEventStore outboxStore;
    private final EventProducer producer;

    @Override
    public void register(IntegrationEventPublishCommand command) {
        switch (command.policy()) {
            case OUTBOX_REQUIRED -> outboxStore.save(command.event(), command.metadata());
            case FIRE_AND_FORGET -> producer.send(command.event());
        }
    }
}
