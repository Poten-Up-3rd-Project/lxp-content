package com.lxp.content.course.application.event.integration;

import com.lxp.common.domain.event.DomainEvent;

public record EventMetadata(
        String aggregateId,
        String aggregateEventType
) {
    public static EventMetadata from(DomainEvent event) {
        return new EventMetadata(
                event.getAggregateId(),
                event.getClass().getSimpleName()
        );
    }
}
