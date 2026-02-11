package com.lxp.content.course.infra.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.port.required.EventProducer;
import com.lxp.content.course.infra.messaging.exception.EventPublishException;
import com.lxp.content.course.infra.messaging.exception.EventSerializationException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer implements EventProducer {

    private static final String COURSE_EXCHANGE = "course.events";
    private static final String CONTENT_EXCHANGE = "content.events";
    private static final String DLQ_EXCHANGE = "dlq.events";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    private String resolveExchange(String eventType) {
        if (eventType.startsWith("course.")) {
            return COURSE_EXCHANGE;
        } else if (eventType.startsWith("qna.")) {
            return CONTENT_EXCHANGE;
        }

        throw new IllegalArgumentException("Unknown event type: " + eventType);
    }

    @Override
    public void send(IntegrationEvent event) {
        String json = serialize(event);
        String exchange = resolveExchange(event.getEventType());

        try {
            rabbitTemplate.convertAndSend(exchange, event.getEventType(), json);
        } catch (Exception e) {
            throw new EventPublishException("Failed to send event: " + event.getEventId(), e);
        }
    }

    @Override
    public void sendToDlq(String payload, String eventType) {
        try {
            rabbitTemplate.convertAndSend(DLQ_EXCHANGE, eventType, payload);
        } catch (Exception e) {
            throw new EventPublishException("Failed to send event to DLQ: " + eventType, e);
        }
    }

    private String serialize(IntegrationEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("Failed to serialize event", e);
        }
    }
}
