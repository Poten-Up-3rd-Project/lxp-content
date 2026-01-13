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

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String EXCHANGE = "course.exchange";


    @Override
    public void send(IntegrationEvent event) {
        try {
            String routingKey = event.getEventType();
            String json = toJson(event);
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, json);
        } catch (EventSerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new EventPublishException("Failed to send event: " + event.getEventId(), e);
        }
    }

    private String toJson(IntegrationEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("Failed to serialize event", e);
        }
    }
}
