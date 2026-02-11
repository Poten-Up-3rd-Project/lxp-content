package com.lxp.content.course.application.event.policy;

import com.lxp.common.application.event.policy.EventPublishPolicy;
import com.lxp.common.infrastructure.persistence.OutboxOptions;
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
        EventPublishPolicy policy = command.policy();

        if (policy.delivery().requiresOutbox()) {
            outboxStore.save(
                    command.event(),
                    command.metadata(),
                    OutboxOptions.from(policy, command.event())
            );
        } else {
            producer.send(command.event());
        }
    }
}
