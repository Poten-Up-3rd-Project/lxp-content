package com.lxp.content.course.infra.integration;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.infra.event.CourseIntegrationEventDispatcher;
import com.lxp.content.course.infra.outbox.CourseOutboxRepository;
import org.junit.jupiter.api.*;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
public class CourseIntegrationEventDispatcherTest {

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
    private CourseOutboxRepository outboxRepository;

    @Autowired
    private CourseIntegrationEventDispatcher dispatcher;

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
    @DisplayName("BEFORE_COMMIT: Outbox 테이블에 이벤트가 저장된다")
    @Transactional
    void onBeforeCommit_savesToOutbox() {
        // given
        CourseCreatedEvent domainEvent = createDomainEvent("course-123");

        // when
        dispatcher.onBeforeCommit(domainEvent);

        // then
        Optional<OutboxEvent> saved = outboxRepository.findByEventId(domainEvent.getEventId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getEventType()).isEqualTo("course.created");
        assertThat(saved.get().getAggregateId()).isEqualTo("course-123");
        assertThat(saved.get().getStatus()).isEqualTo(OutboxEvent.OutboxStatus.PENDING);
    }



    @Test
    @DisplayName("AFTER_COMMIT: Outbox에서 읽어서 RabbitMQ로 발행하고 상태가 PUBLISHED로 변경된다")
    void onAfterCommit_publishesAndMarksPublished() {
        // given
        CourseCreatedEvent domainEvent = createDomainEvent("course-456");

        // Outbox에 먼저 저장 (BEFORE_COMMIT 시뮬레이션)
        dispatcher.onBeforeCommit(domainEvent);

        // when
        dispatcher.onAfterCommit(domainEvent);

        // then - RabbitMQ 메시지 확인
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(QUEUE, 1000);
            assertThat(message).isNotNull();

            String body = new String(message.getBody());
            assertThat(body).contains("course-456");
        });

        // then - Outbox 상태 확인
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Optional<OutboxEvent> outbox = outboxRepository.findByEventId(domainEvent.getEventId());
            assertThat(outbox).isPresent();
            assertThat(outbox.get().getStatus()).isEqualTo(OutboxEvent.OutboxStatus.PUBLISHED);
        });
    }


    @Test
    @DisplayName("IntegrationEvent 형식으로 변환되어 전송된다")
    void dispatch_convertsToIntegrationEvent() {
        // given
        CourseCreatedEvent domainEvent = createDomainEvent("course-789");
        dispatcher.onBeforeCommit(domainEvent);

        // when
        dispatcher.onAfterCommit(domainEvent);

        // then
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(QUEUE, 1000);
            assertThat(message).isNotNull();

            String body = new String(message.getBody());
            JsonNode json = objectMapper.readTree(body);

            // 메타데이터 검증
            assertThat(json.has("eventId")).isTrue();
            assertThat(json.has("correlationId")).isTrue();
            assertThat(json.has("occurredAt")).isTrue();

            // payload 검증
            JsonNode payload = json.get("payload");
            assertThat(payload).isNotNull();
            assertThat(payload.get("courseUuid").asText()).isEqualTo("course-789");
        });
    }

    @Test
    @DisplayName("발행 실패 시 Outbox 상태가 FAILED로 변경된다")
    @Disabled("추후 해당 테스트 따로 격리")
    void onAfterCommit_marksFailedOnError() {
        // given
        CourseCreatedEvent domainEvent = createDomainEvent("course-fail");
        dispatcher.onBeforeCommit(domainEvent);

        // RabbitMQ 연결 끊기 (실패 유도)
        rabbitMQ.stop();

        // when
        dispatcher.onAfterCommit(domainEvent);

        // then
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Optional<OutboxEvent> outbox = outboxRepository.findByEventId(domainEvent.getEventId());
            assertThat(outbox).isPresent();
            assertThat(outbox.get().getStatus()).isEqualTo(OutboxEvent.OutboxStatus.FAILED);
            assertThat(outbox.get().getRetryCount()).isGreaterThan(0);
        });

        // cleanup
        rabbitMQ.start();
    }


    private CourseCreatedEvent createDomainEvent(String courseUuid) {
        return new CourseCreatedEvent(
                courseUuid,
                "instructor-456",
                "Java 기초",
                "자바 기초 강의입니다",
                "thumbnail.png",
                Level.JUNIOR,
                List.of(1L, 2L)
        );
    }

}
