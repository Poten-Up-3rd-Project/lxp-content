package com.lxp.content.course.infra.outbox;

import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.common.infrastructure.persistence.OutboxEventRepository;

import java.util.Optional;

public interface CourseOutboxRepository extends OutboxEventRepository {

    Optional<OutboxEvent> findByEventId(String eventId);
}
