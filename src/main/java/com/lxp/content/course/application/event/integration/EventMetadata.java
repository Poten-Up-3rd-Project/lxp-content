package com.lxp.content.course.application.event.integration;

import com.lxp.content.course.domain.event.CrudEvent;

public record EventMetadata(
        String aggregateId,
        String aggregateEventType
) {
    public static EventMetadata from(CrudEvent event) {
        return new EventMetadata(
                event.getAggregateId(),
                event.getClass().getSimpleName()
        );
    }
}
