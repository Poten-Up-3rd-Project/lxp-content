package com.lxp.content.course.infra.persistence.integration;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.infra.event.CourseIntegrationEventDispatcher;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.List;
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
    private RabbitTemplate rabbitTemplate;

    private RabbitAdmin rabbitAdmin;

    @Autowired
    private CourseIntegrationEventDispatcher dispatcher;

    private static final String EXCHANGE = "course.exchange";
    private static final String QUEUE = "course.created.queue";
    private static final String ROUTING_KEY = "course.created";


    @BeforeEach
    void setUp() {
        // Exchange, Queue, Binding 설정
        rabbitAdmin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());
        rabbitAdmin.declareExchange(new TopicExchange(EXCHANGE));
        rabbitAdmin.declareQueue(new Queue(QUEUE, true));
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue(QUEUE))
                        .to(new TopicExchange(EXCHANGE))
                        .with(ROUTING_KEY)
        );

        // 큐 비우기
        rabbitAdmin.purgeQueue(QUEUE);
    }

    @Test
    @DisplayName("도메인 이벤트 발행 시 RabbitMQ로 메시지가 전송된다")
    void dispatch_sendsMessageToRabbitMQ() {
        // given
        CourseCreatedEvent domainEvent = new CourseCreatedEvent(
                "course-123",
                "instructor-456",
                "Java 기초",
                "자바 기초 강의입니다",
                "thumbnail.png",
                Level.JUNIOR,
                List.of(1L, 2L)
        );

        // when
        dispatcher.dispatch(domainEvent);

        // then - RabbitMQ에서 메시지 수신 확인
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Message message = rabbitTemplate.receive(QUEUE, 1000);

            assertThat(message).isNotNull();

            Assertions.assertNotNull(message);
            String body = new String(message.getBody());
            assertThat(body).contains("course-123");
            assertThat(body).contains("instructor-456");
            assertThat(body).contains("Java 기초");
        });
    }


    @Test
    @DisplayName("IntegrationEvent 형식으로 변환되어 전송된다")
    void dispatch_convertsToIntegrationEvent() throws Exception {
        // given
        CourseCreatedEvent domainEvent = new CourseCreatedEvent(
                "course-789",
                "instructor-111",
                "Spring 입문",
                "스프링 입문 강의",
                "spring.png",
                Level.MIDDLE,
                List.of(3L, 4L)
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // when
        dispatcher.dispatch(domainEvent);

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
            assertThat(payload.get("instructorUuid").asText()).isEqualTo("instructor-111");
            assertThat(payload.get("title").asText()).isEqualTo("Spring 입문");
            assertThat(payload.get("difficulty").asText()).isEqualTo("MIDDLE");
        });
    }

}
