package com.lxp.content.course.application.event;

import com.lxp.common.application.event.policy.EventPolicyRegistry;
import com.lxp.common.application.event.policy.EventPublishPolicy;
import com.lxp.common.application.event.policy.delivery.AtLeastOnce;
import com.lxp.common.application.event.policy.failure.RetryThenDlq;
import com.lxp.common.application.event.policy.ordering.Parallel;
import com.lxp.common.application.event.policy.priority.NormalPriority;
import com.lxp.content.course.application.event.handler.CourseEventPublishingHandler;
import com.lxp.content.course.application.event.mapper.CourseIntegrationEventMapper;
import com.lxp.content.course.application.event.policy.IntegrationEventPublishCommand;
import com.lxp.content.course.application.event.policy.IntegrationEventRegistry;
import com.lxp.content.course.qna.application.event.integration.QnaCreatedIntegrationEvent;
import com.lxp.content.course.qna.application.event.integration.payload.QnaCreatedPayload;
import com.lxp.content.course.qna.domain.event.QnaCreatedEvent;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CourseQnaEventPublishingHandlerTest {

    @Mock
    private IntegrationEventRegistry registry;

    @Mock
    private CourseIntegrationEventMapper mapper;

    @Mock
    private EventPolicyRegistry policyRegistry;

    @InjectMocks
    private CourseEventPublishingHandler handler;

    private QnaCreatedEvent domainEvent;
    private QnaCreatedIntegrationEvent integrationEvent;

    private static final EventPublishPolicy OUTBOX_POLICY = new EventPublishPolicy(
            new AtLeastOnce(),
            new NormalPriority(),
            new Parallel(),
            new RetryThenDlq(3, Duration.ofSeconds(5))
    );

    @BeforeEach
    void setUp() {
        domainEvent = new QnaCreatedEvent(
                "qna-123",
                "course-456",
                "section-789",
                "lecture-012",
                "author-345",
                "질문 제목",
                "질문 내용"
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
            assertThat(command.metadata().aggregateId()).isEqualTo("qna-123");
            assertThat(command.metadata().aggregateEventType()).isEqualTo("QnaCreatedEvent");
        }
    }

    private QnaCreatedIntegrationEvent createIntegrationEvent(QnaCreatedEvent event) {
        QnaCreatedPayload payload = new QnaCreatedPayload(
                new QnaCreatedPayload.Course(event.getCourseUuid(), "코스 제목"),
                new QnaCreatedPayload.Section(event.getSectionUuid(), "섹션 제목"),
                new QnaCreatedPayload.Lecture(event.getLectureUuid(), "강의 제목"),
                new QnaCreatedPayload.Qna(
                        event.getAggregateId(),
                        event.getAuthorId(),
                        event.getTitle(),
                        event.getContent(),
                        event.getOccurredAt()
                )
        );
        return new QnaCreatedIntegrationEvent(
                event.getEventId(),
                event.getOccurredAt(),
                event.getEventId(),
                null,
                payload
        );
    }
}
