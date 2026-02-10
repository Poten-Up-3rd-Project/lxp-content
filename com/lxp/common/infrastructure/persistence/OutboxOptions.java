package com.lxp.common.infrastructure.persistence;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.application.event.policy.EventPublishPolicy;
import com.lxp.common.application.event.policy.delivery.DeliverySemanticType;

import java.time.Duration;

public record OutboxOptions(
        int priority,
        Duration ttl,
        String partitionKey,
        int maxRetries,
        Duration initialBackoff,
        boolean useDlq,
        boolean exactlyOnce
) {
    public static OutboxOptions from(EventPublishPolicy policy, IntegrationEvent event) {
        return new OutboxOptions(
                policy.priority().priority(),
                policy.priority().ttl(),
                policy.ordering().partitionKey(event),
                policy.failure().maxRetries(),
                policy.failure().initialBackoff(),
                policy.failure().useDlq(),
                policy.delivery().type() == DeliverySemanticType.EXACTLY_ONCE
        );
    }
}