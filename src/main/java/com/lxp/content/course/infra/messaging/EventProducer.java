package com.lxp.content.course.infra.messaging;

import com.lxp.common.application.event.IntegrationEvent;

public interface EventProducer {
    void send(IntegrationEvent event);
}
