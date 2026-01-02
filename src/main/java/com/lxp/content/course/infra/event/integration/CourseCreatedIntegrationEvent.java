package com.lxp.content.course.infra.event.integration;


import com.lxp.common.application.event.BaseIntegrationEventEnvelope;
import com.lxp.content.course.infra.event.integration.payload.CourseCreatedPayload;


public class CourseCreatedIntegrationEvent extends BaseIntegrationEventEnvelope<CourseCreatedPayload> {
    private static final String SOURCE = "lxp.course.service";

    public CourseCreatedIntegrationEvent(String correlationId, String causationId, CourseCreatedPayload payload) {
        super(SOURCE, correlationId, causationId, payload);
    }


    @Override
    public String getEventType() {
        return "course.created";
    }


}