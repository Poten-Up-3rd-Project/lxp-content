package com.lxp.content.course.infra.messaging.producer;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.content.course.application.port.required.EventProducer;
import com.lxp.content.course.infra.messaging.exception.EventPublishException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RabbitMQProducer implements EventProducer {

    private static final String COURSE_EXCHANGE = "course.events";
    private static final String CONTENT_EXCHANGE = "content.events";
    private static final String DLQ_EXCHANGE = "dlq.events";

    private final RabbitTemplate rabbitTemplate;


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
        String exchange = resolveExchange(event.getEventType());

        try {
            rabbitTemplate.convertAndSend(exchange, event.getEventType(), event);
        } catch (Exception e) {
            throw new EventPublishException("Failed to send event: " + event.getEventId(), e);
        }
    }

    @Override
    public void sendToDlq(String payload, String eventType) {
        try {
            Message message = MessageBuilder
                    .withBody(payload.getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
            rabbitTemplate.send(DLQ_EXCHANGE, eventType, message);
        } catch (Exception e) {
            throw new EventPublishException("Failed to send event to DLQ: " + eventType, e);
        }
    }
}
