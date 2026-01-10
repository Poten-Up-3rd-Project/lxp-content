package com.lxp.content.course.application.event.handler;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.event.integration.EventMetadata;
import com.lxp.content.course.application.event.mapper.CourseIntegrationEventMapper;
import com.lxp.content.course.application.event.policy.DeliveryPolicy;
import com.lxp.content.course.application.event.policy.DeliveryPolicyResolver;
import com.lxp.content.course.application.event.policy.IntegrationEventPublishCommand;
import com.lxp.content.course.application.event.policy.IntegrationEventRegistry;
import com.lxp.content.course.domain.event.CrudEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CourseEventPublishingHandler {

    private final IntegrationEventRegistry registry;
    private final CourseIntegrationEventMapper mapper;
    private final DeliveryPolicyResolver policyResolver;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(CrudEvent event) {
        IntegrationEvent integrationEvent = mapper.toIntegrationEvent(event);
        DeliveryPolicy policy = policyResolver.resolve(event);

        IntegrationEventPublishCommand command =
                switch (policy) {
                    case OUTBOX_REQUIRED ->
                            IntegrationEventPublishCommand.outbox(
                                    integrationEvent,
                                    EventMetadata.from(event)
                            );

                    case FIRE_AND_FORGET ->
                            IntegrationEventPublishCommand.fireAndForget(
                                    integrationEvent
                            );
                };

        registry.register(command);

    }
}