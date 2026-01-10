package com.lxp.content.course.application.port.required;

import com.lxp.common.application.event.IntegrationEvent;

public interface EventProducer {
    void send(IntegrationEvent event);
}
