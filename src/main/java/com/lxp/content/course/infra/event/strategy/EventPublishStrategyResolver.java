package com.lxp.content.course.infra.event.strategy;

import com.lxp.content.course.application.event.EventPublishStrategy;
import com.lxp.content.course.domain.event.CrudEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventPublishStrategyResolver {
    private final List<EventPublishStrategy> strategies;

    public EventPublishStrategy resolve(CrudEvent event) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(event))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No strategy found for: " + event.getClass().getSimpleName()));
    }
}
