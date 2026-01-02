package com.lxp.content.course.infra.event;

import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.infra.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.content.course.infra.event.integration.payload.CourseCreatedPayload;
import org.springframework.stereotype.Component;

@Component
public class CourseIntegrationEventMapper {

    public CourseCreatedIntegrationEvent toCreatedEvent(CourseCreatedEvent event) {
        CourseCreatedPayload payload = new CourseCreatedPayload(
                event.getAggregateId(),
                event.getInstructorUuid(),
                event.getTitle(),
                event.getDescription(),
                event.getThumbnailUrl(),
                event.getDifficulty().name(),
                event.getTagIds()
        );

        return new CourseCreatedIntegrationEvent(
                event.getEventId(),  // correlationId
                null,  // causationId
                payload
        );
    }
}
