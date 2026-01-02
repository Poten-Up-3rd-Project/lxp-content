package com.lxp.content.course.infra.event;

import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.infra.messaging.exception.EventPublishException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CourseIntegrationEventDispatcher {
    private final CourseIntegrationEventMapper mapper;
    private final CourseIntegrationEventPublisher publisher;

    @Async
    @Retryable(
            retryFor = EventPublishException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dispatch(CourseCreatedEvent event) {
        publisher.publish(mapper.toCreatedEvent(event));
    }

}