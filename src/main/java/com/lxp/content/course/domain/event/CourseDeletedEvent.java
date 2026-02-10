package com.lxp.content.course.domain.event;

import com.lxp.common.domain.event.BaseDomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CourseDeletedEvent extends BaseDomainEvent implements CrudEvent{
    private final LocalDateTime deletedAt;

    public CourseDeletedEvent(String aggregateId, LocalDateTime deletedAt) {
        super(aggregateId);
        this.deletedAt = deletedAt;
    }

    protected CourseDeletedEvent(String eventId, String aggregateId, LocalDateTime occurredAt, LocalDateTime deletedAt) {
        super(eventId, aggregateId, occurredAt);
        this.deletedAt = deletedAt;
    }

    @Override
    public CrudType getCrudType() {
        return CrudType.DELETED;
    }
}
