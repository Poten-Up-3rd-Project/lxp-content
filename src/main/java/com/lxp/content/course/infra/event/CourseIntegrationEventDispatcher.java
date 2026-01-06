package com.lxp.content.course.infra.event;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.event.EventPublishStrategy;
import com.lxp.content.course.domain.event.CrudEvent;
import com.lxp.content.course.infra.event.strategy.EventPublishStrategyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseIntegrationEventDispatcher {
    private final CourseIntegrationEventMapper mapper;
    private final EventPublishStrategyResolver strategyResolver;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onBeforeCommit(CrudEvent event) {
        IntegrationEvent integrationEvent = mapper.toIntegrationEvent(event);
        EventPublishStrategy strategy = strategyResolver.resolve(event);
        strategy.prepare(integrationEvent, event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAfterCommit(CrudEvent event) {
        IntegrationEvent integrationEvent = mapper.toIntegrationEvent(event);
        EventPublishStrategy strategy = strategyResolver.resolve(event);
        strategy.publish(integrationEvent, event);
    }

}