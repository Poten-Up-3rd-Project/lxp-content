package com.lxp.content.course.infra.outbox;

import com.lxp.common.application.event.IntegrationEvent;
import com.lxp.common.infrastructure.persistence.OutboxEvent;
import com.lxp.content.course.application.event.integration.EventMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxEventStoreAdapterTest {

    @Mock
    private CourseOutboxRepository outboxRepository;

    @Mock
    private OutboxEventSerializer serializer;

    @InjectMocks
    private OutboxEventStoreAdapter adapter;

    @Mock
    private IntegrationEvent integrationEvent;

    private EventMetadata metadata;

    @BeforeEach
    void setUp() {
        metadata = new EventMetadata("course-123", "CourseCreatedEvent");
    }

    @Test
    @DisplayName("IntegrationEvent를 OutboxEvent로 변환하여 저장한다")
    void save_convertsAndSavesOutboxEvent() {
        // given
        given(integrationEvent.getEventId()).willReturn("event-123");
        given(integrationEvent.getEventType()).willReturn("course.created");
        given(integrationEvent.getOccurredAt()).willReturn(LocalDateTime.now());
        given(serializer.serialize(integrationEvent)).willReturn("{\"payload\":\"test\"}");

        // when
        adapter.save(integrationEvent, metadata);

        // then
        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(captor.capture());

        OutboxEvent saved = captor.getValue();
        assertThat(saved.getEventId()).isEqualTo("event-123");
        assertThat(saved.getEventType()).isEqualTo("course.created");
        assertThat(saved.getAggregateId()).isEqualTo("course-123");
        assertThat(saved.getAggregateType()).isEqualTo("CourseCreatedEvent");
        assertThat(saved.getStatus()).isEqualTo(OutboxEvent.OutboxStatus.PENDING);
    }
}