package com.lxp.content.course.application.port.required;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.event.integration.EventMetadata;

public interface OutboxEventStore {
    void save(IntegrationEvent event, EventMetadata metadata);
}