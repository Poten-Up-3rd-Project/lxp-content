package com.lxp.content.course.infra.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.application.port.out.IntegrationEventPublisher;
import com.lxp.content.course.infra.messaging.EventProducer;
import com.lxp.content.course.infra.messaging.exception.EventPublishException;
import com.lxp.content.course.infra.messaging.exception.EventSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseIntegrationEventPublisher implements IntegrationEventPublisher {

    //TODO: 추후 메시지 브로커 연동 시 수정 필요
    // outbox 패턴도입 고려
    private final EventProducer producer;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(IntegrationEvent event) {
        try {
            producer.send(event);
        } catch (EventSerializationException e) {
            log.error("Serialization failed - bug?: {}", event.getEventId(), e);
        } catch (EventPublishException e) {
            // 재시도 가능, 추후 Outbox에 저장
            log.warn("Publish failed, will retry: {}", event.getEventId(), e);
            throw e;
        }
    }

    @Override
    public void publish(String topic, IntegrationEvent event) {
        return;
    }


    // 추후 outBox에 필요함
    private String toJson(IntegrationEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
