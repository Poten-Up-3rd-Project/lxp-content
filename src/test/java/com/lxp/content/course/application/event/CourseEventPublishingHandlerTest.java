package com.lxp.content.course.application.event;

import com.lxp.common.application.event.policy.EventPolicyRegistry;
import com.lxp.common.application.event.policy.EventPublishPolicy;
import com.lxp.common.application.event.policy.delivery.AtLeastOnce;
import com.lxp.common.application.event.policy.delivery.AtMostOnce;
import com.lxp.common.application.event.policy.failure.DropOnFailure;
import com.lxp.common.application.event.policy.failure.RetryThenDlq;
import com.lxp.common.application.event.policy.ordering.Parallel;
import com.lxp.common.application.event.policy.priority.LowPriority;
import com.lxp.common.application.event.policy.priority.NormalPriority;
import com.lxp.content.course.application.event.handler.CourseEventPublishingHandler;
import com.lxp.content.course.application.event.integration.CourseCreatedIntegrationEvent;
import com.lxp.content.course.application.event.integration.payload.CourseCreatedPayload;
import com.lxp.content.course.application.event.mapper.CourseIntegrationEventMapper;
import com.lxp.content.course.application.event.policy.IntegrationEventPublishCommand;
import com.lxp.content.course.application.event.policy.IntegrationEventRegistry;
import com.lxp.content.course.domain.event.CourseCreatedEvent;
import com.lxp.content.course.domain.model.enums.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseEventPublishingHandlerTest {

    @Mock
    private IntegrationEventRegistry registry;

    @Mock
    private CourseIntegrationEventMapper mapper;

    @Mock
    private EventPolicyRegistry policyRegistry;

    @InjectMocks
    private CourseEventPublishingHandler handler;

    private CourseCreatedEvent domainEvent;
    private CourseCreatedIntegrationEvent integrationEvent;

    private static final EventPublishPolicy OUTBOX_POLICY = new EventPublishPolicy(
            new AtLeastOnce(),
            new NormalPriority(),
            new Parallel(),
            new RetryThenDlq(3, Duration.ofSeconds(5))
    );

    private static final EventPublishPolicy FIRE_AND_FORGET_POLICY = new EventPublishPolicy(
            new AtMostOnce(),
            new LowPriority(),
            new Parallel(),
            new DropOnFailure()
    );

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

    @Nested
    @DisplayName("handleBeforeCommit (Outbox 필요)")
    class HandleBeforeCommit {

        @Test
        @DisplayName("AtLeastOnce 정책이면 Registry에 등록한다")
        void registersWhenOutboxRequired() {
            // given
            given(policyRegistry.resolve(domainEvent)).willReturn(OUTBOX_POLICY);
            given(mapper.toIntegrationEvent(domainEvent)).willReturn(integrationEvent);

            // when
            handler.handleBeforeCommit(domainEvent);

            // then
            ArgumentCaptor<IntegrationEventPublishCommand> captor =
                    ArgumentCaptor.forClass(IntegrationEventPublishCommand.class);

            verify(registry).register(captor.capture());

            IntegrationEventPublishCommand command = captor.getValue();
            assertThat(command.policy().delivery().requiresOutbox()).isTrue();
            assertThat(command.event().getEventId()).isEqualTo(domainEvent.getEventId());
            assertThat(command.metadata()).isNotNull();
            assertThat(command.metadata().aggregateId()).isEqualTo("course-123");
            assertThat(command.metadata().aggregateEventType()).isEqualTo("CourseCreatedEvent");
        }

        @Test
        @DisplayName("AtMostOnce 정책이면 Registry에 등록하지 않는다")
        void doesNotRegisterWhenFireAndForget() {
            // given
            given(policyRegistry.resolve(domainEvent)).willReturn(FIRE_AND_FORGET_POLICY);

            // when
            handler.handleBeforeCommit(domainEvent);

            // then
            verify(registry, never()).register(any());
        }
    }

    @Nested
    @DisplayName("handleAfterCommit (Fire-and-Forget)")
    class HandleAfterCommit {

        @Test
        @DisplayName("AtMostOnce 정책이면 Registry에 등록한다")
        void registersWhenFireAndForget() {
            // given
            given(policyRegistry.resolve(domainEvent)).willReturn(FIRE_AND_FORGET_POLICY);
            given(mapper.toIntegrationEvent(domainEvent)).willReturn(integrationEvent);

            // when
            handler.handleAfterCommit(domainEvent);

            // then
            ArgumentCaptor<IntegrationEventPublishCommand> captor =
                    ArgumentCaptor.forClass(IntegrationEventPublishCommand.class);

            verify(registry).register(captor.capture());

            IntegrationEventPublishCommand command = captor.getValue();
            assertThat(command.policy().delivery().requiresOutbox()).isFalse();
            assertThat(command.event().getEventId()).isEqualTo(domainEvent.getEventId());
            assertThat(command.metadata()).isNull();
        }

        @Test
        @DisplayName("AtLeastOnce 정책이면 Registry에 등록하지 않는다")
        void doesNotRegisterWhenOutboxRequired() {
            // given
            given(policyRegistry.resolve(domainEvent)).willReturn(OUTBOX_POLICY);

            // when
            handler.handleAfterCommit(domainEvent);

            // then
            verify(registry, never()).register(any());
        }
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
