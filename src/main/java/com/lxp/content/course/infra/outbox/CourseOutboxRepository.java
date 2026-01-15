package com.lxp.content.course.infra.outbox;

import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.common.infrastructure.persistence.OutboxEventRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseOutboxRepository extends OutboxEventRepository {

    Optional<OutboxEvent> findByEventId(String eventId);

    @Query("""
        SELECT o FROM OutboxEvent o
        WHERE o.status = :status
        AND o.retryCount < :maxRetry
        ORDER BY o.createdAt ASC
        LIMIT :limit
        """)
    List<OutboxEvent> findByStatusAndRetryCountLessThanOrderByOccurredAtAsc(
            @Param("status") OutboxEvent.OutboxStatus status,
            @Param("maxRetry") int maxRetry,
            @Param("limit") int limit
    );

    @Modifying
    @Query("""
        DELETE FROM OutboxEvent o
        WHERE o.status = 'PUBLISHED'
        AND o.publishedAt < :before
        """)
    int deletePublishedEventsBefore(@Param("before") LocalDateTime before);
}
