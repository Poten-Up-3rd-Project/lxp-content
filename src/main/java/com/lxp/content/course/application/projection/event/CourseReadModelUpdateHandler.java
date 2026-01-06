package com.lxp.content.course.application.projection.event;

import com.lxp.common.infrastructure.cqrs.ReadModelUpdater;
import com.lxp.content.common.exception.ExternalServiceException;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.application.port.required.dto.TagResult;
import com.lxp.content.course.application.projection.CourseReadModel;
import com.lxp.content.course.application.projection.repository.CourseReadRepository;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.event.CrudEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseReadModelUpdateHandler implements ReadModelUpdater<CrudEvent> {
    private final CourseReadRepository readRepository;
    private final UserQueryPort userQueryPort;
    private final TagQueryPort tagQueryPort;


    @Retryable(
            retryFor = ExternalServiceException.class,  // common에서 가져옴
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void update(CrudEvent event) {
        switch (event.getCrudType()) {
            case CREATED -> onCreate((CourseCreatedEvent) event);
//            case UPDATED -> onUpdate((CourseUpdatedEvent) event);
//            case DELETED -> onDelete((CourseDeletedEvent) event);
        }
    }

    @Override
    public Class<CrudEvent> supportedEventType() {
        return CrudEvent.class;
    }

    private void onCreate(CourseCreatedEvent event) {
        System.out.println("Handling CourseCreatedEvent for course ID: " + event.getAggregateId());
        String instructorName = userQueryPort.getInstructorInfo(event.getInstructorUuid())
                .name();
        List<TagResult> tagResults = tagQueryPort.findTagByIds(event.getTagIds());
        List<CourseReadModel.TagReadModel> tags = tagResults.stream()
                .map(t -> new CourseReadModel.TagReadModel(t.id(), t.content(), t.color(), t.variant()))
                .toList();

        CourseReadModel model = new CourseReadModel(
                event.getAggregateId(),
                event.getInstructorUuid(),
                instructorName,
                event.getThumbnailUrl(),
                event.getTitle(),
                event.getDescription(),
                event.getDifficulty(),
                tags,
                event.getOccurredAt(),
                event.getOccurredAt()
        );

        readRepository.save(model);
    }

//    private void onUpdate(CourseUpdatedEvent event) {
//        repository.findByUuid(event.getAggregateId())
//                .ifPresent(entity -> entity.update(event));
//    }
//
//    private void onDelete(CourseDeletedEvent event) {
//        repository.deleteByUuid(event.getAggregateId());
//    }
}