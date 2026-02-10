package com.lxp.content.course.application.event.mapper;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.content.course.application.event.integration.payload.CourseCreatedPayload;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.qna.application.event.integration.QnaCreatedIntegrationEvent;
import com.lxp.content.course.qna.application.event.integration.payload.QnaCreatedPayload;
import com.lxp.content.course.qna.application.event.mapper.QnaIntegrationEventMapper;
import com.lxp.content.course.qna.application.port.out.ReadCourseStructurePort;
import com.lxp.content.course.qna.domain.event.QnaCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseIntegrationEventMapper {

    private final QnaIntegrationEventMapper qnaIntegrationEventMapper;

    public IntegrationEvent toIntegrationEvent(CrudEvent event) {
        if (event instanceof CourseCreatedEvent e) {
            return toCreatedEvent(e);
        }
        if (event instanceof QnaCreatedEvent e) {
            return qnaIntegrationEventMapper.toCreated(e);
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
