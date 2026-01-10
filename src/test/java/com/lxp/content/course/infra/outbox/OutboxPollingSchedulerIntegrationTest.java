package com.lxp.content.course.infra.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.SimpleLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class OutboxPollingSchedulerIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.12-management");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }

    @TestConfiguration
    static class TestShedLockConfig {
        @Bean
        @Primary
        public LockProvider testLockProvider() {
            return lockConfiguration -> Optional.of((SimpleLock) () -> {});
        }
    }

    @Autowired
    private CourseOutboxRepository outboxRepository;

    @Autowired
    private OutboxPollingScheduler scheduler;

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

        outboxRepository.deleteAll();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Scheduler가 PENDING 이벤트를 발행하고 PUBLISHED로 변경한다")
    void pollAndPublish_publishesPendingEvents() {
        // given
        OutboxEvent outbox = createOutboxEvent("course-456");
        outboxRepository.saveAndFlush(outbox);

        // when
        scheduler.pollAndPublish();

        // then - RabbitMQ 메시지 확인
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(QUEUE, 1000);
            assertThat(message).isNotNull();

            String body = new String(message.getBody());
            assertThat(body).contains("course-456");
        });

        // then - Outbox 상태 확인
        OutboxEvent published = outboxRepository.findByEventId(outbox.getEventId()).orElseThrow();
        assertThat(published.getStatus()).isEqualTo(OutboxEvent.OutboxStatus.PUBLISHED);
        assertThat(published.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("IntegrationEvent 형식으로 변환되어 전송된다")
    void pollAndPublish_sendsIntegrationEventFormat() throws Exception {
        // given
        OutboxEvent outbox = createOutboxEvent("course-789");
        outboxRepository.saveAndFlush(outbox);

        // when
        scheduler.pollAndPublish();

        // then
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(QUEUE, 1000);
            assertThat(message).isNotNull();

            String body = new String(message.getBody());
            JsonNode json = objectMapper.readTree(body);

            // 메타데이터 검증
            assertThat(json.has("eventId")).isTrue();
            assertThat(json.has("eventType")).isTrue();
            assertThat(json.has("occurredAt")).isTrue();

            // payload 검증
            JsonNode payload = json.get("payload");
            assertThat(payload).isNotNull();
            assertThat(payload.get("courseUuid").asText()).isEqualTo("course-789");
        });
    }

    @Test
    @DisplayName("발행 실패 시 FAILED 상태가 되고 retryCount가 증가한다")
    void pollAndPublish_marksFailedOnError() {
        // given - 잘못된 payload
        OutboxEvent invalidOutbox = createInvalidOutboxEvent("course-invalid");
        outboxRepository.saveAndFlush(invalidOutbox);

        // when
        scheduler.pollAndPublish();

        // then
        OutboxEvent failed = outboxRepository.findByEventId(invalidOutbox.getEventId()).orElseThrow();
        assertThat(failed.getStatus()).isEqualTo(OutboxEvent.OutboxStatus.FAILED);
        assertThat(failed.getRetryCount()).isEqualTo(1);
        assertThat(failed.getLastError()).isNotNull();
    }

    @Test
    @DisplayName("재시도 스케줄러가 FAILED 이벤트를 다시 시도한다")
    void retryFailedEvents_retriesFailedEvents() {
        // given - FAILED 상태의 유효한 이벤트
        OutboxEvent failedOutbox = createOutboxEvent("course-retry");
        failedOutbox.markAsFailed("Previous error");
        outboxRepository.saveAndFlush(failedOutbox);

        // when
        scheduler.retryFailedEvents();

        // then
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            OutboxEvent retried = outboxRepository.findByEventId(failedOutbox.getEventId()).orElseThrow();
            assertThat(retried.getStatus()).isEqualTo(OutboxEvent.OutboxStatus.PUBLISHED);
        });
    }

    @Test
    @DisplayName("최대 재시도 횟수 초과 시 더 이상 시도하지 않는다")
    void retryFailedEvents_ignoresExhaustedRetries() {
        // given - 재시도 횟수 초과
        OutboxEvent exhaustedOutbox = createExhaustedOutboxEvent("course-exhausted");
        outboxRepository.saveAndFlush(exhaustedOutbox);

        int initialRetryCount = exhaustedOutbox.getRetryCount();

        // when
        scheduler.retryFailedEvents();

        // then - 변화 없음
        OutboxEvent unchanged = outboxRepository.findByEventId(exhaustedOutbox.getEventId()).orElseThrow();
        assertThat(unchanged.getRetryCount()).isEqualTo(initialRetryCount);
        assertThat(unchanged.getStatus()).isEqualTo(OutboxEvent.OutboxStatus.FAILED);
    }

    // ===== Helper Methods =====

    private OutboxEvent createOutboxEvent(String courseUuid) {
        String eventId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        String payload = String.format("""
            {
                "eventId": "%s",
                "eventType": "course.created",
                "occurredAt": "%s",
                "source": "lxp.course.service",
                "correlationId": "%s",
                "causationId": null,
                "payload": {
                    "courseUuid": "%s",
                    "instructorUuid": "instructor-456",
                    "title": "Java 기초",
                    "description": "자바 기초 강의입니다",
                    "thumbnailUrl": "thumbnail.png",
                    "difficulty": "JUNIOR",
                    "tagIds": [1, 2]
                }
            }
            """, eventId, now, eventId, courseUuid);

        return new OutboxEvent(
                eventId,
                "course.created",
                "CourseCreatedEvent",
                courseUuid,
                payload,
                now
        );
    }

    private OutboxEvent createInvalidOutboxEvent(String courseUuid) {
        String eventId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        return new OutboxEvent(
                eventId,
                "course.created",
                "CourseCreatedEvent",
                courseUuid,
                "{ invalid json }",
                now
        );
    }

    private OutboxEvent createExhaustedOutboxEvent(String courseUuid) {
        String eventId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        OutboxEvent outbox = new OutboxEvent(
                eventId,
                "course.created",
                "CourseCreatedEvent",
                courseUuid,
                "{ invalid }",
                now
        );

        // 3번 실패 처리
        outbox.markAsFailed("Error 1");
        outbox.markAsFailed("Error 2");
        outbox.markAsFailed("Error 3");

        return outbox;
    }
}