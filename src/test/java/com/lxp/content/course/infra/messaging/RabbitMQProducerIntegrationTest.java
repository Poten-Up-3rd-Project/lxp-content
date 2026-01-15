package com.lxp.content.course.infra.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lxp.content.course.application.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.content.course.application.event.integration.payload.CourseCreatedPayload;
import com.lxp.content.course.infra.messaging.producer.RabbitMQProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class RabbitMQProducerIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.12-management");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }

    @Autowired
    private RabbitMQProducer producer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private RabbitAdmin rabbitAdmin;
    private ObjectMapper objectMapper;

    private static final String EXCHANGE = "course.exchange";
    private static final String QUEUE = "course.created.queue";
    private static final String ROUTING_KEY = "course.created";

    @BeforeEach
    void setUp() {
        rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());
        rabbitAdmin.declareExchange(new TopicExchange(EXCHANGE));
        rabbitAdmin.declareQueue(new Queue(QUEUE, true));
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(QUEUE))
                        .to(new TopicExchange(EXCHANGE))
                        .with(ROUTING_KEY)
        );
        rabbitAdmin.purgeQueue(QUEUE);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("IntegrationEvent를 RabbitMQ로 발행한다")
    void send_publishesEventToRabbitMQ() {
        // given
        CourseCreatedIntegrationEvent event = createIntegrationEvent("course-123");

        // when
        producer.send(event);

        // then
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(QUEUE, 1000);
            assertThat(message).isNotNull();

            String body = new String(message.getBody());
            assertThat(body).contains("course-123");
        });
    }

    @Test
    @DisplayName("발행된 메시지가 올바른 JSON 형식이다")
    void send_publishesValidJsonFormat() throws Exception {
        // given
        CourseCreatedIntegrationEvent event = createIntegrationEvent("course-456");

        // when
        producer.send(event);

        // then
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(QUEUE, 1000);
            assertThat(message).isNotNull();

            String body = new String(message.getBody());
            JsonNode json = objectMapper.readTree(body);

            // 메타데이터 검증
            assertThat(json.get("eventId").asText()).isEqualTo(event.getEventId());
            assertThat(json.get("eventType").asText()).isEqualTo("course.created");
            assertThat(json.has("occurredAt")).isTrue();
            assertThat(json.has("correlationId")).isTrue();

            // payload 검증
            JsonNode payload = json.get("payload");
            assertThat(payload).isNotNull();
            assertThat(payload.get("courseUuid").asText()).isEqualTo("course-456");
            assertThat(payload.get("instructorUuid").asText()).isEqualTo("instructor-456");
            assertThat(payload.get("title").asText()).isEqualTo("Java 기초");
        });
    }

    @Test
    @DisplayName("여러 이벤트를 순서대로 발행한다")
    void send_publishesMultipleEventsInOrder() {
        // given
        CourseCreatedIntegrationEvent event1 = createIntegrationEvent("course-001");
        CourseCreatedIntegrationEvent event2 = createIntegrationEvent("course-002");
        CourseCreatedIntegrationEvent event3 = createIntegrationEvent("course-003");

        // when
        producer.send(event1);
        producer.send(event2);
        producer.send(event3);

        // then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Message msg1 = rabbitTemplate.receive(QUEUE, 1000);
            Message msg2 = rabbitTemplate.receive(QUEUE, 1000);
            Message msg3 = rabbitTemplate.receive(QUEUE, 1000);

            assertThat(msg1).isNotNull();
            assertThat(msg2).isNotNull();
            assertThat(msg3).isNotNull();

            assertThat(new String(msg1.getBody())).contains("course-001");
            assertThat(new String(msg2.getBody())).contains("course-002");
            assertThat(new String(msg3.getBody())).contains("course-003");
        });
    }

    @Test
    @DisplayName("올바른 routing key로 발행된다")
    void send_usesCorrectRoutingKey() {
        // given
        String differentQueue = "course.updated.queue";
        rabbitAdmin.declareQueue(new Queue(differentQueue, true));
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(differentQueue))
                        .to(new TopicExchange(EXCHANGE))
                        .with("course.updated")
        );

        CourseCreatedIntegrationEvent event = createIntegrationEvent("course-789");

        // when
        producer.send(event);

        // then - course.created 큐에만 메시지 있어야 함
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Message createdMsg = rabbitTemplate.receive(QUEUE, 1000);
            Message updatedMsg = rabbitTemplate.receive(differentQueue, 500);

            assertThat(createdMsg).isNotNull();
            assertThat(updatedMsg).isNull();
        });
    }

    // ===== Helper Methods =====

    private CourseCreatedIntegrationEvent createIntegrationEvent(String courseUuid) {
        String eventId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        CourseCreatedPayload payload = new CourseCreatedPayload(
                courseUuid,
                "instructor-456",
                "Java 기초",
                "자바 기초 강의입니다",
                "thumbnail.png",
                "JUNIOR",
                List.of(1L, 2L)
        );

        return new CourseCreatedIntegrationEvent(
                eventId,
                now,
                eventId,      // correlationId
                null,         // causationId
                payload
        );
    }
}