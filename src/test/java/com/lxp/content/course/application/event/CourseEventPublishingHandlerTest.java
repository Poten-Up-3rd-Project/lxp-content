package com.lxp.content.course.application.event;

import com.lxp.content.course.application.event.handler.CourseEventPublishingHandler;
import com.lxp.content.course.application.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.content.course.application.event.integration.payload.CourseCreatedPayload;
import com.lxp.content.course.application.event.mapper.CourseIntegrationEventMapper;
import com.lxp.content.course.application.event.policy.DeliveryPolicy;
import com.lxp.content.course.application.event.policy.DeliveryPolicyResolver;
import com.lxp.content.course.application.event.policy.IntegrationEventPublishCommand;
import com.lxp.content.course.application.event.policy.IntegrationEventRegistry;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.model.enums.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseEventPublishingHandlerTest {

    @Mock
    private IntegrationEventRegistry registry;

    @Mock
    private CourseIntegrationEventMapper mapper;

    @Mock
    private DeliveryPolicyResolver policyResolver;

    @InjectMocks
    private CourseEventPublishingHandler handler;

    private CourseCreatedEvent domainEvent;
    private CourseCreatedIntegrationEvent integrationEvent;

    @BeforeEach
    void setUp() {
        domainEvent = new CourseCreatedEvent(
                "course-123",
                "instructor-456",
                "Java 기초",
                "자바 기초 강의입니다",
                "thumbnail.png",
                Level.JUNIOR,
                List.of(1L, 2L)
        );

        integrationEvent = createIntegrationEvent(domainEvent);
    }

    @Test
    @DisplayName("도메인 이벤트를 IntegrationEventPublishCommand로 변환하여 Registry에 publish한다")
    void handle_publishesIntegrationEventCommand() {
        // given
        given(mapper.toIntegrationEvent(domainEvent)).willReturn(integrationEvent);
        given(policyResolver.resolve(domainEvent)).willReturn(DeliveryPolicy.OUTBOX_REQUIRED);

        // when
        handler.handle(domainEvent);

        // then
        ArgumentCaptor<IntegrationEventPublishCommand> commandCaptor =
                ArgumentCaptor.forClass(IntegrationEventPublishCommand.class);

        verify(registry).register(commandCaptor.capture());

        IntegrationEventPublishCommand command = commandCaptor.getValue();

        assertThat(command.event().getEventId())
                .isEqualTo(domainEvent.getEventId());

        assertThat(command.policy())
                .isEqualTo(DeliveryPolicy.OUTBOX_REQUIRED);

        assertThat(command.metadata().aggregateId())
                .isEqualTo("course-123");

        assertThat(command.metadata().aggregateEventType())
                .isEqualTo("CourseCreatedEvent");
    }

    @Test
    @DisplayName("OUTBOX_REQUIRED 정책으로 publish된다")
    void handle_withOutboxRequiredPolicy() {
        // given
        given(mapper.toIntegrationEvent(domainEvent)).willReturn(integrationEvent);
        given(policyResolver.resolve(domainEvent)).willReturn(DeliveryPolicy.OUTBOX_REQUIRED);

        // when
        handler.handle(domainEvent);

        // then
        ArgumentCaptor<IntegrationEventPublishCommand> captor =
                ArgumentCaptor.forClass(IntegrationEventPublishCommand.class);

        verify(registry).register(captor.capture());

        assertThat(captor.getValue().policy())
                .isEqualTo(DeliveryPolicy.OUTBOX_REQUIRED);
    }

    @Test
    @DisplayName("FIRE_AND_FORGET 정책으로 publish된다")
    void handle_withFireAndForgetPolicy() {
        // given
        given(mapper.toIntegrationEvent(domainEvent)).willReturn(integrationEvent);
        given(policyResolver.resolve(domainEvent)).willReturn(DeliveryPolicy.FIRE_AND_FORGET);

        // when
        handler.handle(domainEvent);

        // then
        ArgumentCaptor<IntegrationEventPublishCommand> captor =
                ArgumentCaptor.forClass(IntegrationEventPublishCommand.class);

        verify(registry).register(captor.capture());

        assertThat(captor.getValue().policy())
                .isEqualTo(DeliveryPolicy.FIRE_AND_FORGET);

        assertThat(captor.getValue().metadata())
                .isNull(); // FIRE_AND_FORGET는 metadata 없음
    }

    private CourseCreatedIntegrationEvent createIntegrationEvent(CourseCreatedEvent event) {
        return new CourseCreatedIntegrationEvent(
                event.getEventId(),
                event.getOccurredAt(),
                event.getEventId(),
                null,
                new CourseCreatedPayload(
                        event.getAggregateId(),
                        event.getInstructorUuid(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getThumbnailUrl(),
                        event.getDifficulty().name(),
                        event.getTagIds()
                )
        );
    }
}
