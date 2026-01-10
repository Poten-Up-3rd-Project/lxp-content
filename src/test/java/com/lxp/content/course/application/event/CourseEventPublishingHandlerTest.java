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
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.never;
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

    @Nested
    @DisplayName("handleBeforeCommit (OUTBOX_REQUIRED)")
    class HandleBeforeCommit {

        @Test
        @DisplayName("OUTBOX_REQUIRED 정책이면 Registry에 등록한다")
        void registersWhenOutboxRequired() {
            // given
            given(policyResolver.resolve(domainEvent)).willReturn(DeliveryPolicy.OUTBOX_REQUIRED);
            given(mapper.toIntegrationEvent(domainEvent)).willReturn(integrationEvent);

            // when
            handler.handleBeforeCommit(domainEvent);

            // then
            ArgumentCaptor<IntegrationEventPublishCommand> captor =
                    ArgumentCaptor.forClass(IntegrationEventPublishCommand.class);

            verify(registry).register(captor.capture());

            IntegrationEventPublishCommand command = captor.getValue();
            assertThat(command.policy()).isEqualTo(DeliveryPolicy.OUTBOX_REQUIRED);
            assertThat(command.event().getEventId()).isEqualTo(domainEvent.getEventId());
            assertThat(command.metadata()).isNotNull();
            assertThat(command.metadata().aggregateId()).isEqualTo("course-123");
            assertThat(command.metadata().aggregateEventType()).isEqualTo("CourseCreatedEvent");
        }

        @Test
        @DisplayName("FIRE_AND_FORGET 정책이면 Registry에 등록하지 않는다")
        void doesNotRegisterWhenFireAndForget() {
            // given
            given(policyResolver.resolve(domainEvent)).willReturn(DeliveryPolicy.FIRE_AND_FORGET);

            // when
            handler.handleBeforeCommit(domainEvent);

            // then
            verify(registry, never()).register(any());
        }
    }

    @Nested
    @DisplayName("handleAfterCommit (FIRE_AND_FORGET)")
    class HandleAfterCommit {

        @Test
        @DisplayName("FIRE_AND_FORGET 정책이면 Registry에 등록한다")
        void registersWhenFireAndForget() {
            // given
            given(policyResolver.resolve(domainEvent)).willReturn(DeliveryPolicy.FIRE_AND_FORGET);
            given(mapper.toIntegrationEvent(domainEvent)).willReturn(integrationEvent);

            // when
            handler.handleAfterCommit(domainEvent);

            // then
            ArgumentCaptor<IntegrationEventPublishCommand> captor =
                    ArgumentCaptor.forClass(IntegrationEventPublishCommand.class);

            verify(registry).register(captor.capture());

            IntegrationEventPublishCommand command = captor.getValue();
            assertThat(command.policy()).isEqualTo(DeliveryPolicy.FIRE_AND_FORGET);
            assertThat(command.event().getEventId()).isEqualTo(domainEvent.getEventId());
            assertThat(command.metadata()).isNull();
        }

        @Test
        @DisplayName("OUTBOX_REQUIRED 정책이면 Registry에 등록하지 않는다")
        void doesNotRegisterWhenOutboxRequired() {
            // given
            given(policyResolver.resolve(domainEvent)).willReturn(DeliveryPolicy.OUTBOX_REQUIRED);

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