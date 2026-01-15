package com.lxp.content.course.application.event.policy;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.event.integration.EventMetadata;

public record IntegrationEventPublishCommand(
        IntegrationEvent event,
        DeliveryPolicy policy,
        EventMetadata metadata
) {
    public static IntegrationEventPublishCommand outbox(
            IntegrationEvent event,
            EventMetadata metadata
    ) {
        return new IntegrationEventPublishCommand(
                event,
                DeliveryPolicy.OUTBOX_REQUIRED,
                metadata
        );
    }

    public static IntegrationEventPublishCommand fireAndForget(
            IntegrationEvent event
    ) {
        return new IntegrationEventPublishCommand(
                event,
                DeliveryPolicy.FIRE_AND_FORGET,
                null
        );
    }
}