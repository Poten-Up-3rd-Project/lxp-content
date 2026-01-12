package com.lxp.content.course.application.event.policy;


public interface IntegrationEventRegistry {
    void register(IntegrationEventPublishCommand command);
}
