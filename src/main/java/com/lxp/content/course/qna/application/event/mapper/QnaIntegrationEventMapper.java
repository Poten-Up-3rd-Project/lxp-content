package com.lxp.content.course.qna.application.event.mapper;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.qna.application.event.integration.QnaCreatedIntegrationEvent;
import com.lxp.content.course.qna.application.event.integration.payload.QnaCreatedPayload;
import com.lxp.content.course.qna.application.port.out.ReadCourseStructurePort;
import com.lxp.content.course.qna.domain.event.QnaCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QnaIntegrationEventMapper {

    private final ReadCourseStructurePort readCourseStructurePort;

    public IntegrationEvent toIntegrationEvent(CrudEvent event) {
        if (event instanceof QnaCreatedEvent e) {
            return toCreated(e);
        }
        throw new IllegalArgumentException("Unknown event: " + event.getClass());
    }

    public QnaCreatedIntegrationEvent toCreated(QnaCreatedEvent e) {
        ReadCourseStructurePort.Titles titles = readCourseStructurePort.titlesOf(e.getCourseUuid(), e.getSectionUuid(), e.getLectureUuid());
        QnaCreatedPayload payload = new QnaCreatedPayload(
            new QnaCreatedPayload.Course(e.getCourseUuid(), titles.courseTitle()),
            new QnaCreatedPayload.Section(e.getSectionUuid(), titles.sectionTitle()),
            new QnaCreatedPayload.Lecture(e.getLectureUuid(), titles.lectureTitle()),
            new QnaCreatedPayload.Qna(e.getAggregateId(), e.getAuthorId(), e.getTitle(), e.getContent(), e.getOccurredAt())
        );
        return new QnaCreatedIntegrationEvent(
            e.getEventId(),
            e.getOccurredAt(),
            e.getEventId(), // correlationId
            null, // causationId
            payload
        );
    }
}
