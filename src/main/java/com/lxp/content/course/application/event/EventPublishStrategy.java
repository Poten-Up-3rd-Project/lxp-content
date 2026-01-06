package com.lxp.content.course.application.event;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.domain.event.CrudEvent;

public interface EventPublishStrategy {
    boolean supports(CrudEvent event);

    void prepare(IntegrationEvent integrationEvent, CrudEvent event);

    void publish(IntegrationEvent integrationEvent, CrudEvent event);
}
