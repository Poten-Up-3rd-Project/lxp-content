package com.lxp.content.course.application.event.mapper;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.application.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.content.course.application.event.integration.payload.CourseCreatedPayload;
import org.springframework.stereotype.Component;

@Component
public class CourseIntegrationEventMapper {

    public IntegrationEvent toIntegrationEvent(CrudEvent event) {
        if (event instanceof CourseCreatedEvent e) {
            return toCreatedEvent(e);
        }
//        else if (event instanceof CourseUpdatedEvent e) {
//            return toUpdatedEvent(e);
//        } else if (event instanceof CourseDeletedEvent e) {
//            return toDeletedEvent(e);
//        }
        throw new IllegalArgumentException("Unknown event: " + event.getClass());
    }



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
                event.getEventId(),
                event.getOccurredAt(),
                event.getEventId(),  // correlationId
                null,  // causationId
                payload
        );
    }
}
